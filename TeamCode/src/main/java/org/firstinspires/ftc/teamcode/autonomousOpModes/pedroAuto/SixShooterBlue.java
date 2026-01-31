package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "6-Ball Blue", group = "Autonomous")
public class SixShooterBlue extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    private DcMotorEx intakeMotor;
    private DcMotorEx spindexer;

    private ShooterLogic shooter = new ShooterLogic();

    private boolean shotsTriggered = false;

    public enum PathState {
        DRIVE_FROM_GOAL,
        SHOOT_PRELOADED,
        DRIVE_TO_INTAKE,
        INTAKE_TO_PICKUP,
        PICKUP_TO_SHOOT,
        SHOOT_RELOADED,
        STRAFE_OUT
    }

    private PathState pathState;

    private final Pose startPose = new Pose(24, 120, Math.toRadians(135));
    private final Pose shootPose = new Pose(48, 96, Math.toRadians(135));
    private final Pose intakePose = new Pose(48, 84, Math.toRadians(180));
    private final Pose pickupPose = new Pose(17.5, 84, Math.toRadians(180));
    private final Pose endPose = new Pose(40, 88, Math.toRadians(135));

    private PathChain startToShoot, shootToIntake, intakeToPickup, pickupToShoot, shootToFinalPose;

    public void buildPaths() {
        startToShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        shootToIntake = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, intakePose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), intakePose.getHeading())
                .build();
        intakeToPickup = follower.pathBuilder()
                .addPath(new BezierLine(intakePose, pickupPose))
                .setLinearHeadingInterpolation(intakePose.getHeading(), pickupPose.getHeading())
                .build();
        pickupToShoot = follower.pathBuilder()
                .addPath(new BezierLine(pickupPose,shootPose))
                .setLinearHeadingInterpolation(pickupPose.getHeading(), shootPose.getHeading())
                .build();
        shootToFinalPose = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();
    }

    public void statePathUpdatifier() {
        switch (pathState) {
            case DRIVE_FROM_GOAL:
                follower.followPath(startToShoot, true);
                setPathState(PathState.SHOOT_PRELOADED);
                break;
            case SHOOT_PRELOADED:
                if (!follower.isBusy()) {
                    //requested shots??
                    if (!shotsTriggered) {
                        shooter.fireShots(3);
                        shotsTriggered = true;
                    } else if (!shooter.isBusy()) {
                        follower.followPath(shootToIntake, true);
                        setPathState(PathState.INTAKE_TO_PICKUP);
                    }
                }
                break;
            case INTAKE_TO_PICKUP:
                if (!follower.isBusy()) {
                    intakeMotor.setPower(1);
                    if (pathTimer.getElapsedTimeSeconds() > 1) {
                        follower.followPath(intakeToPickup);
                        spindexer.setTargetPosition(spindexer.getCurrentPosition() + 538);
                        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                        spindexer.setPower(0.5);
                        setPathState(PathState.PICKUP_TO_SHOOT);
                    }
                }
                break;
            case PICKUP_TO_SHOOT:
                if (!follower.isBusy()) {
                    spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    intakeMotor.setPower(0);
                    follower.followPath(pickupToShoot);
                    setPathState(PathState.SHOOT_RELOADED);
                }
                break;
            case SHOOT_RELOADED:
                if (!follower.isBusy()) {
                    //requested shots??
                    if (!shotsTriggered) {
                        shooter.fireShots(3);
                        shotsTriggered = true;
                    } else if (!shooter.isBusy()) {
                        follower.followPath(shootToFinalPose, true);
                        setPathState(PathState.STRAFE_OUT);
                    }
                }
                break;
            case STRAFE_OUT:
                if (!follower.isBusy()) {
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
        shotsTriggered = false;

    }

    public void init() {
        pathState = PathState.DRIVE_FROM_GOAL;
        pathTimer = new Timer();
        opModeTimer = new Timer();
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intakeMotor");
        spindexer = hardwareMap.get(DcMotorEx.class, "spindexifier");
        spindexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        follower = Constants.createFollower(hardwareMap);
        shooter.init(hardwareMap);
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
        statePathUpdatifier();

        telemetry.addData("Current Path State of Doom", pathState.toString());
        telemetry.addData("Robo-X (position that is)", follower.getPose().getX());
        telemetry.addData("Robo-Y (position that is)", follower.getPose().getY());
        telemetry.addData("Angle of bot/heading", follower.getPose().getHeading());
        telemetry.addData("Path of Time (path time)", pathTimer.getElapsedTimeSeconds());
    }
}
