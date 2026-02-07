package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic.ShooterLogic;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Disabled
@Autonomous(name = "You're out of Line! Red Ver.", group = "Autonomous")
public class MoveOutOfLineRed extends OpMode {
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

    private final Pose startPose = new Pose(120, 120, Math.toRadians(45));

    private PathChain startToShoot, shootToEnd, curveOffTheLine;
    public void buildPaths() {
        curveOffTheLine = follower.pathBuilder().addPath(
                        new BezierCurve(
                                new Pose(120.000, 120.000),
                                new Pose(106.616, 101.168),
                                new Pose(96.827, 116.694)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(45))

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
