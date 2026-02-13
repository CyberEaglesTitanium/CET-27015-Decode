package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.ShooterLogic;
import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.SpindexAutoLogic;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "Intake Test II", group = "Autonomous")
public class IntakeTest2 extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    private SpindexAutoLogic spindex = new SpindexAutoLogic();
    private ShooterLogic shooter = new ShooterLogic();

    private boolean artifactsToEat = false;

    public enum PathState {
        DRIVE_FROM_GOAL,
        DRIVE_TO_INTAKE,
        INTAKE_ON,
        DRIVE_TO_INTAKE_1,
        INDEX_1,
        DRIVE_TO_INTAKE_2,
        INDEX_2,

        INTAKE_1,
        STRAFE_OUT
    }

    private PathState pathState;

    private final Pose startPose = new Pose(24, 120, Math.toRadians(135));
    private final Pose shootPose = new Pose(48, 96, Math.toRadians(135));
    private final Pose intake1endPose = new Pose(24, 84, Math.toRadians(180));
    private final Pose intake1startPose = new Pose(48, 84, Math.toRadians(180));
    private final Pose endPose = new Pose(40, 88, Math.toRadians(135));

    private PathChain startToShoot, shootToEnd, intake1ToShoot, shootToIntake1;

    public void buildPaths() {
        startToShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        intake1ToShoot = follower.pathBuilder()
                .addPath(new BezierLine(intake1endPose, shootPose))
                .setLinearHeadingInterpolation(intake1endPose.getHeading(), shootPose.getHeading())
                .build();
        shootToIntake1 = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, intake1startPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), intake1startPose.getHeading())
                .build();
        shootToEnd = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();
    }

    public void statePathUpdatifier() {
        switch (pathState) {
            case DRIVE_FROM_GOAL:
                follower.followPath(startToShoot, true);
                setPathState(PathState.DRIVE_TO_INTAKE);
                break;
            case DRIVE_TO_INTAKE:
                if (!follower.isBusy()) {
                    follower.followPath(shootToIntake1);
                    setPathState(PathState.INTAKE_1);
                }
                break;
            case INTAKE_1:
                if (!follower.isBusy()) {
                    //requested shots??
                    if (!artifactsToEat) {
                        spindex.intakeBalls(3);
                        artifactsToEat = true;
                    } else if (!spindex.isBusy()) {
                        follower.followPath(intake1ToShoot, true);
                        setPathState(PathState.STRAFE_OUT);
                    }
                }
                break;
            case STRAFE_OUT:
                if (!follower.isBusy()) {
                    follower.followPath(shootToEnd);
                    telemetry.addLine("Done all paths");
                }
                break;
            default:
                telemetry.addLine("Nothing running, all is good and boring");
                break;
        }
    }

    public void setPathState(PathState newState) {
        pathState = newState;
        pathTimer.resetTimer();
        artifactsToEat = false;

    }

    public void init() {
        pathState = PathState.DRIVE_FROM_GOAL;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        follower = Constants.createFollower(hardwareMap);
        shooter.init(hardwareMap);
        spindex.init(hardwareMap);
        buildPaths();
        follower.setPose(startPose);
    }

    public void start() {
        opModeTimer.resetTimer();
        setPathState(pathState);
    }

    public void loop() {
        follower.update();
        shooter.update();
        spindex.update();
        statePathUpdatifier();

        telemetry.addData("Current Path State of Doom", pathState.toString());
        telemetry.addData("Robo-X (position that is)", follower.getPose().getX());
        telemetry.addData("Robo-Y (position that is)", follower.getPose().getY());
        telemetry.addData("Angle of bot/heading", follower.getPose().getHeading());
        telemetry.addData("Path of Time (path time)", pathTimer.getElapsedTimeSeconds());
    }
}
