package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.experiments;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.quickdraw.ShooterLogicQuickdraw;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Autonomous(name = "Blue Auto (Quickdraw)", group = "Autonomous")
public class QThreeBlue extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    private ShooterLogicQuickdraw shooter = new ShooterLogicQuickdraw();

    private boolean shotsTriggered = false;

    public enum PathState {
        DRIVE_FROM_GOAL,
        SHOOT_PRELOADED,
        STRAFE_OUT
    }

    private PathState pathState;

    private final Pose startPose = new Pose(24, 120, Math.toRadians(135));
    private final Pose shootPose = new Pose(48, 96, Math.toRadians(135));
    private final Pose endPose = new Pose(40, 88, Math.toRadians(135));

    private PathChain startToShoot, shootToEnd;

    public void buildPaths() {
        startToShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
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
                setPathState(PathState.SHOOT_PRELOADED);
                break;
            case SHOOT_PRELOADED:
                if (!follower.isBusy()) {
                    //requested shots??
                    if (!shotsTriggered) {
                        shooter.fireShots(3, 3);
                        shotsTriggered = true;
                    } else if (!shooter.isBusy()) {
                        follower.followPath(shootToEnd, true);
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
