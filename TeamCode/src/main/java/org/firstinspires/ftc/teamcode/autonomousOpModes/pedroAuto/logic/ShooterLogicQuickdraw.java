package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto.logic;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ShooterLogicQuickdraw {
    private DcMotorEx spindexer;
    private DcMotorEx shootMotor;
    private DcMotorEx shootMotor2;

    private Servo flickServo;
    private Servo loadServo;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum FlywheelState {
        IDLE,
        SPIN_UP,
        LOAD,
        HAMMER,
        LAUNCH,
        RESET
    }

    private FlywheelState flywheelState;

    private double FLICK_STARTER_POS = 0.5;
    private double LOAD_UNLOADED_POS = 1;
    private double FLICK_HAMMER_POS = -1;
    private double LOAD_LOAD_POS = 0;

    private int BASE_INDEXER_POS = 178;

    private double FLICK_HAMMER_TIME = 0.3;
    private double LOAD_LOAD_TIME = 0.3;
    private double LOAD_UNLOAD_TIME = 0.3;

    public int shotsRemaining = 0;
    private int index = 5;

    private double TARGET_FLYWHEEL_POWER = 0.5;

    private double MAX_FLYWHEEL_TIME = 2;

    public void init (HardwareMap hwMap) {
        spindexer = hwMap.get(DcMotorEx.class, "spindexifier");
        flickServo = hwMap.get(Servo.class, "shootGate1");
        loadServo = hwMap.get(Servo.class, "shootGate2");
        shootMotor = hwMap.get(DcMotorEx.class, "shootMotor");
        shootMotor2 = hwMap.get(DcMotorEx.class, "shootMotor2");

        shootMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shootMotor2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
//        shootMotor2.setDirection(DcMotorEx.Direction.REVERSE);

        flywheelState = FlywheelState.IDLE;

        shootMotor.setPower(0);
        flickServo.setPosition(FLICK_STARTER_POS);
        loadServo.setPosition(LOAD_UNLOADED_POS);
    }

    public void update() {
        switch (flywheelState) {
            case IDLE:
                if (shotsRemaining > 0) {
                    flickServo.setPosition(FLICK_STARTER_POS);
                    loadServo.setPosition(LOAD_UNLOADED_POS);
                    shootMotor.setPower(TARGET_FLYWHEEL_POWER);
                    shootMotor2.setPower(TARGET_FLYWHEEL_POWER);

                    stateTimer.reset();
                    flywheelState = FlywheelState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                if (shotsRemaining == 3 && stateTimer.seconds() > MAX_FLYWHEEL_TIME) {
                    loadServo.setPosition(LOAD_LOAD_POS);
                    stateTimer.reset();
                    shotsRemaining--;
                    flywheelState = FlywheelState.LOAD;
                } else if (shotsRemaining <= 2) {
                    loadServo.setPosition(LOAD_LOAD_POS);
                    stateTimer.reset();
                    shotsRemaining--;
                    flywheelState = FlywheelState.LOAD;
                }
                break;
            case LOAD:
                if (stateTimer.seconds() > FLICK_HAMMER_TIME) {
                    loadServo.setPosition(LOAD_UNLOADED_POS);
                    if (shotsRemaining == 0) {
                        flywheelState = FlywheelState.RESET;
                    } else {
                        stateTimer.reset();
                        flywheelState = FlywheelState.HAMMER;
                    }
//                    if (stateTimer.seconds() > 1) {
//                        flickServo.setPosition(FLICK_HAMMER_POS);
//                    }
                }
                break;
            case HAMMER:
                if (stateTimer.seconds() > FLICK_HAMMER_TIME) {
                    flickServo.setPosition(FLICK_HAMMER_POS);
                    stateTimer.reset();
                    flywheelState = FlywheelState.LAUNCH;
                }
                break;
            case LAUNCH:
                if (stateTimer.seconds() > LOAD_LOAD_TIME) {
                    flickServo.setPosition(FLICK_STARTER_POS);
                    if (shotsRemaining == 2) {
                        spinUseLeft();
                        stateTimer.reset();
                    } else {
                        stateTimer.reset();
                    }
                    flywheelState = FlywheelState.RESET;
                }
                break;
            case RESET:
                if (stateTimer.seconds() > LOAD_UNLOAD_TIME) {
                    if (shotsRemaining > 0) {
                        stateTimer.reset();

                        flywheelState = FlywheelState.SPIN_UP;
                    } else {
                        shootMotor.setPower(0);
                        shootMotor2.setPower(0);
//                        index = 9;
                        flywheelState = FlywheelState.IDLE;
                    }
                }
                break;
        }
    }

    public void fireShots(int numberOfShots, int indexAgain) {
        if (flywheelState == FlywheelState.IDLE) {
            shotsRemaining = numberOfShots;
            index = indexAgain;
        }
    }

    public boolean isBusy() {
        return flywheelState != FlywheelState.IDLE;
    }

    // it might be that ai is useful. i still don't like it though.
    public void spindexStep() {
        int currentPosOfIndexer = spindexer.getCurrentPosition();
        int nextPos = BASE_INDEXER_POS + currentPosOfIndexer;
        spindexer.setTargetPosition(nextPos);
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }

    void spinUseLeft() {
        index -= 1;
        spindexer.setTargetPosition(BASE_INDEXER_POS * (5 - index));
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }
}
