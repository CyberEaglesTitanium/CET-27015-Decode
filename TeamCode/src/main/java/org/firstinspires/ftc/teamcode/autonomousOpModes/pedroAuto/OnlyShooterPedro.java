package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.pedropathing.follower.Follower;
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
@Autonomous(name = "only shooters in pedropathing", group = "Autonomous")
public class OnlyShooterPedro extends OpMode {
    private Follower follower;
    private Timer pathTimer, opModeTimer;

    private ShooterLogic shooter = new ShooterLogic();

    private boolean shotsTriggered = false;

    public enum PathState {
        SHOOT_PRELOADED
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
            case SHOOT_PRELOADED:
                if (!follower.isBusy()) {
                    //requested shots??
                    if (!shotsTriggered) {
                        shooter.fireShots(3);
                        if (pathTimer.getElapsedTimeSeconds() > 10) {
                            shotsTriggered = true;
                        }
                    } else if (shotsTriggered && !follower.isBusy()) {
                        telemetry.addLine("artifacts launched, check physical location to see if this is correct");
                    }
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
        pathState = PathState.SHOOT_PRELOADED;
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
