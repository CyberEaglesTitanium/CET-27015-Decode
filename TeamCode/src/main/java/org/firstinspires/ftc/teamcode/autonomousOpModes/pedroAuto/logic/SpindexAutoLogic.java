package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class SpindexAutoLogic {
    private Follower follower;

    private DcMotorEx spindexer;
    private DcMotorEx shootMotor;
    private DcMotorEx intakeMotor;

    private ShooterLogic shooter = new ShooterLogic();

    private Servo flickServo;
    private Servo loadServo;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum FlywheelState {
        IDLE,
        EAT_ARTIFACTS,
        EAT_ARTIFACTS_II,
        EAT_ARTIFACTS_III,
        GETTING_OUT_OF_HERE,
        RESET
    }

    private final Pose intakeStart = new Pose(48, 84, 180);
    private final Pose intakePosition1 = new Pose(36, 84, 180);
    private final Pose intakePosition2 = new Pose(30, 84, 180);
    private final Pose intakePosition3 = new Pose(24, 84, 180);
    private final Pose shootPos = new Pose(48, 96, 135);

    private PathChain startToIntake1, intake1toIntake2, intake2toIntake3, intake3toShootPos;

    private FlywheelState flywheelState;

    private double FLICK_STARTER_POS = 0.5;
    private double LOAD_UNLOADED_POS = 1;

    private int BASE_INDEXER_POS = 178;

    private double PATH_TIME = 1;

    public int amountToIntake = 0;
    private int index = 3;

    private double TARGET_FLYWHEEL_POWER = 0.5;

    private double MAX_FLYWHEEL_TIME = 4;

    public void buildPaths() {
        startToIntake1 = follower.pathBuilder()
                .addPath(new BezierLine(intakeStart, intakePosition1))
                .setLinearHeadingInterpolation(intakeStart.getHeading(), intakePosition1.getHeading())
                .build();
        intake1toIntake2 = follower.pathBuilder()
                .addPath(new BezierLine(intakePosition1, intakePosition2))
                .setLinearHeadingInterpolation(intakePosition1.getHeading(), intakePosition2.getHeading())
                .build();
        intake2toIntake3 = follower.pathBuilder()
                .addPath(new BezierLine(intakePosition2, intakePosition3))
                .setLinearHeadingInterpolation(intakePosition2.getHeading(), intakePosition3.getHeading())
                .build();
        intake3toShootPos = follower.pathBuilder()
                .addPath(new BezierLine(intakePosition3, shootPos))
                .setLinearHeadingInterpolation(intakePosition3.getHeading(), shootPos.getHeading())
                .build();
    }

    public void init (HardwareMap hwMap) {
        spindexer = hwMap.get(DcMotorEx.class, "spindexifier");
        flickServo = hwMap.get(Servo.class, "shootGate1");
        loadServo = hwMap.get(Servo.class, "shootGate2");
        shootMotor = hwMap.get(DcMotorEx.class, "shootMotor");
        intakeMotor = hwMap.get(DcMotorEx.class, "intakeMotor");

        spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spindexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shootMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        flywheelState = FlywheelState.IDLE;

        shootMotor.setPower(0);
        flickServo.setPosition(FLICK_STARTER_POS);
        loadServo.setPosition(LOAD_UNLOADED_POS);
    }

    public void update() {
        switch (flywheelState) {
            case IDLE:
                if (amountToIntake > 0) {
                    intakeMotor.setPower(1);
                    stateTimer.reset();
                    flywheelState = FlywheelState.EAT_ARTIFACTS;
                }
                break;
            case EAT_ARTIFACTS:
                if (stateTimer.seconds() >= 2 && !follower.isBusy()) {
                    follower.followPath(startToIntake1);
                    if (!follower.isBusy()) {
                        spinUseLeft();
                        flywheelState = FlywheelState.EAT_ARTIFACTS_II;
                    }
                }
                break;
            case EAT_ARTIFACTS_II:
                if (!follower.isBusy() && stateTimer.seconds() >= 4) {
                    follower.followPath(intake1toIntake2);
                    if (!follower.isBusy()) {
                        spinUseLeft();
                        flywheelState = FlywheelState.EAT_ARTIFACTS_III;
                    }
                }
                break;
            case EAT_ARTIFACTS_III:
                if (!follower.isBusy() && stateTimer.seconds() >= 6) {
                    follower.followPath(intake2toIntake3);
                    if (!follower.isBusy()) {
                        spinUseLeft();
                        flywheelState = FlywheelState.RESET;
                    }
                }
                break;
            case RESET:
                if (stateTimer.seconds() > PATH_TIME) {
                        intakeMotor.setPower(0);
                        amountToIntake = 0;
                        flywheelState = FlywheelState.IDLE;
                }
                break;
        }
    }

    public void intakeBalls(int numberOfShots) {
        if (flywheelState == FlywheelState.IDLE) {
            amountToIntake = numberOfShots;
        }
    }

    public boolean isBusy() {
        return flywheelState != FlywheelState.IDLE;
    }

    void spinUseLeft() {
        index -= 1;
        spindexer.setTargetPosition(BASE_INDEXER_POS * (3 - index));
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }
}
