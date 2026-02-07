package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.ShooterLogic;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Disabled
@Autonomous(name = "You're out of Line! Blue Ver.", group = "Autonomous")
public class MoveOutOfLineBlue extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    private ShooterLogic shooter = new ShooterLogic();

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

    private PathChain startToShoot, shootToEnd, curveOffTheLine;
    public void buildPaths() {
        startToShoot = follower.pathBuilder()
                .addPath(new BezierLine(startPose, shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(), shootPose.getHeading())
                .build();
        shootToEnd = follower.pathBuilder()
                .addPath(new BezierLine(shootPose, endPose))
                .setLinearHeadingInterpolation(shootPose.getHeading(), endPose.getHeading())
                .build();
        curveOffTheLine = follower.pathBuilder().addPath(
                        new BezierCurve(
                                new Pose(24.000, 120.000),
                                new Pose(42.411, 94.797),
                                new Pose(54.775, 113.336)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(135))

                .build();
    }

    public void statePathUpdatifier() {
        switch (pathState) {
            case DRIVE_FROM_GOAL:
                follower.followPath(curveOffTheLine, true);
                setPathState(PathState.SHOOT_PRELOADED);
                break;
            case SHOOT_PRELOADED:
                telemetry.addLine("It curved! Off of the line! Holy $%!#, it worked!");
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
