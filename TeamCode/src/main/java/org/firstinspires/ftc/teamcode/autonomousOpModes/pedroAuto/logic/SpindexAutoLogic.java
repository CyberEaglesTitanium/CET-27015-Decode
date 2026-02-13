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

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

public class SpindexAutoLogic {
    private Follower follower;

    private DcMotorEx spindexer;
    private DcMotorEx shootMotor;
    private DcMotorEx intakeMotor;

    private ShooterLogic shooter = new ShooterLogic();

    private Servo flickServo;
    private Servo loadServo;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum IntakeState {
        IDLE,
        EAT_ARTIFACTS,
        EAT_ARTIFACTS_II,
        EAT_ARTIFACTS_III,
        GETTING_OUT_OF_HERE,
        RESET
    }

    private final Pose intakeStart = new Pose(48, 84, Math.toRadians(180));
    private final Pose intakePosition1 = new Pose(36, 84, Math.toRadians(180));
    private final Pose intakePosition2 = new Pose(30, 84, Math.toRadians(180));
    private final Pose intakePosition3 = new Pose(24, 84, Math.toRadians(180));
    private final Pose shootPos = new Pose(48, 96, Math.toRadians(135));

    private PathChain startToIntake1, intake1toIntake2, intake2toIntake3, intake3toShootPos;

    private IntakeState intakeState;

    private double FLICK_STARTER_POS = 0.5;
    private double LOAD_UNLOADED_POS = 1;
    private double FLICK_HAMMER_POS = -1;
    private double LOAD_LOAD_POS = 0;

    private int BASE_INDEXER_POS = 178;

    private double PATH_TIME = 0.5;

    public int amountToIntake = 0;
    private int index = 3; // check if it resets every time

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
        intakeMotor = hwMap.get(DcMotorEx.class, "intakeMotor");

        follower = Constants.createFollower(hwMap);

        spindexer.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        spindexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        intakeMotor.setPower(0);
        flickServo.setPosition(FLICK_STARTER_POS);
        loadServo.setPosition(LOAD_UNLOADED_POS);

        buildPaths();
        intakeState = IntakeState.IDLE;
    }

    public void update() {
        switch (intakeState) {
            case IDLE:
                if (amountToIntake > 0) {
                    intakeMotor.setPower(1);
                    loadServo.setPosition(LOAD_UNLOADED_POS);
                    stateTimer.reset();
                    intakeState = IntakeState.EAT_ARTIFACTS;

                }
                break;
            case EAT_ARTIFACTS:
                if (stateTimer.seconds() >= 0.5 && !follower.isBusy()) {
                    follower.followPath(startToIntake1);
                    if (!follower.isBusy()) {
                        spinUseLeft();
                        amountToIntake -= 1;
                        stateTimer.reset();
                        intakeState = IntakeState.EAT_ARTIFACTS_II;
                    }
                }
                break;
            case EAT_ARTIFACTS_II:
                if (!follower.isBusy() && stateTimer.seconds() >= 0.5) {
                    follower.followPath(intake1toIntake2);
                    if (!follower.isBusy()) {
                        spinUseLeft();
                        if (stateTimer.seconds() >= 1.5) {
                            flickServo.setPosition(FLICK_HAMMER_POS);
                        }
                        amountToIntake -= 1;
                        stateTimer.reset();
                        intakeState = IntakeState.EAT_ARTIFACTS_III;
                    }
                }
                break;
            case EAT_ARTIFACTS_III:
                if (!follower.isBusy() && stateTimer.seconds() >= 0.5) {
                    flickServo.setPosition(FLICK_STARTER_POS);
                    follower.followPath(intake2toIntake3);
                    if (!follower.isBusy()) {
                        spinUseLeft();
                        stateTimer.reset();
                        intakeState = IntakeState.RESET;
                    }
                }
                break;
            case RESET:
                if (stateTimer.seconds() > PATH_TIME) {
                        intakeMotor.setPower(0);
                        amountToIntake -= 1;
                        intakeState = IntakeState.IDLE;
                }
                break;
        }
    }

    public void intakeBalls(int numberOfArtifacts) {
        if (intakeState == IntakeState.IDLE) {
            amountToIntake = numberOfArtifacts;
        }
    }

    public boolean isBusy() {
        return intakeState != IntakeState.IDLE;
    }

    public void loop() {
        follower.update();
    }

    void spinUseLeft() {
        index -= 1;
        spindexer.setTargetPosition(BASE_INDEXER_POS * (3 - index));
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }
}
