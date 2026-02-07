package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.TelemetryManager;
import com.bylazar.telemetry.PanelsTelemetry;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.follower.Follower;
import com.pedropathing.paths.PathChain;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@Autonomous(name = "Backwards Spindexifier Pedro Edition Blue", group = "Autonomous")
@Configurable // Panels
public class BlueBackwardsAutoLaunchPedro extends OpMode {
    private TelemetryManager panelsTelemetry; // Panels Telemetry instance
    public Follower follower; // Pedro Pathing follower instance
    private int pathState; // Current autonomous path state (state machine)
    private Paths paths;
    private Timer pathTimer, actionTimer, opmodeTimer, justLoadTimer, justFlickTimer, pauseTimer; // Paths defined in the Paths class

    private boolean justLoadTimerIsLoaded, justFlickTimerIsLoaded, pauseTimerIsLoaded;

    private int index;
    private int currentPos;

    private DcMotorEx spindexifier;
    private DcMotorEx shootMotor;
    private Servo shootGate1; // Servo flicking device
    private Servo shootGate2; // Servo loading device



    @Override
    public void init() {
        pathTimer = new Timer();
        actionTimer = new Timer();
        opmodeTimer = new Timer();
        justLoadTimer = new Timer();
        justFlickTimer = new Timer();
        pauseTimer = new Timer();
        opmodeTimer.resetTimer();
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        pauseTimerIsLoaded = false;

        spindexifier = hardwareMap.get(DcMotorEx.class, "spindexifier");
        shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");

        shootGate1 = hardwareMap.get(Servo.class, "shootGate1");
        shootGate2 = hardwareMap.get(Servo.class, "shootGate2");

        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(new Pose(24, 120, Math.toRadians(135)));

        paths = new Paths(follower); // Build paths

        currentPos = -178;

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void init_loop() {}

    @Override
    public void loop() {
        follower.update(); // Update Pedro Pathing
        autonomousPathUpdate();
        checkFlags();

        // Log values to Panels and Driver Station
        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void start() {
        opmodeTimer.resetTimer();
        setPathState(0);
    }

    public static class Paths {
        public PathChain Path1;
        public PathChain Path2;

        public Paths(Follower follower) {
            Path1 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(24.000, 120.000),

                                    new Pose(48.000, 96.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(135))

                    .build();

            Path2 = follower.pathBuilder().addPath(
                            new BezierLine(
                                    new Pose(48.000, 96.000),

                                    new Pose(40.000, 88.000)
                            )
                    ).setLinearHeadingInterpolation(Math.toRadians(135), Math.toRadians(135))

                    .build();
        }
    }


    public void autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                shootMotor.setPower(1);
                follower.followPath(paths.Path1);
                setPathState(1);
                break;
            case 1:
                if (!follower.isBusy()) {
                    if (pathTimer.getElapsedTimeSeconds() > 5) {
                        follower.resumePathFollowing();
                        setPathState(2);
                    } else {
                        follower.pausePathFollowing();
                    }
                }
                break;
            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(paths.Path2);
                    setPathState(3);
                }
                break;
            case 3:
                if (!follower.isBusy()) {
                    setPathState(-1);
                }
                break;
        }


        // Add your state machine Here
        // Access paths with paths.pathName
        // Refer to the Pedro Pathing Docs (Auto Example) for an example state machine
    }
    public void setPathState(int pState) {
        pathState = pState;
        pathTimer.resetTimer();
    }

    void spinIndex() {
        spindexifier.setTargetPosition(currentPos * index);
    }

    void resetSpindexEncoder() {
        spindexifier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    void spinUseRight() {
        index += 1;
//            if (index == 4) {
//                index = 1;
//            }
        spinIndex();
        spindexifier.setPower(0.3);
    }

    void spinUseLeft() {
        index -= 1;
//            if (index == 0) {
//                index = 3;
//            }
        spinIndex();
        spindexifier.setPower(0.3);
    }

    void justFlick() {
        justFlickTimer.resetTimer();
        justFlickTimerIsLoaded = true;
        shootGate1.setPosition(-1);
    }
    void justFlickEnd() {
        shootGate1.setPosition(0.5);
    }
    void justLoad() {
        justLoadTimer.resetTimer();
        justLoadTimerIsLoaded = true;
        shootGate2.setPosition(0);
    }
    void justLoadEnd() {
        shootGate2.setPosition(1);
    }

    void checkFlags() {
        if (justLoadTimerIsLoaded) {
            if (justLoadTimer.getElapsedTime() >= 200) {
                justLoadTimerIsLoaded = false;
                justLoadEnd();
            }
        }

        if (justFlickTimerIsLoaded) {
            if (justFlickTimer.getElapsedTime() >= 200) {
                justFlickTimerIsLoaded = false;
                justFlickEnd();
            }
        }

        if (pauseTimerIsLoaded) {
            if (pauseTimer.getElapsedTime() >= 850) {
                pauseTimerIsLoaded = false;
            }
        }
    }
}
