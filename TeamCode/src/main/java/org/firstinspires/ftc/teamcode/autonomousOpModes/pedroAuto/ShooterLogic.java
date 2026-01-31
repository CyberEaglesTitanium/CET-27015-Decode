package org.firstinspires.ftc.teamcode.autonomousOpModes.pedroAuto;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

public class ShooterLogic {
    private DcMotorEx spindexer;
    private DcMotorEx shootMotor;

    private Servo flickServo;
    private Servo loadServo;

    private ElapsedTime stateTimer = new ElapsedTime();

    private enum FlywheelState {
        IDLE,
        SPIN_UP,
        LOAD,
        LAUNCH,
        RESET
    }

    private FlywheelState flywheelState;

    private double FLICK_STARTER_POS = 0.5;
    private double LOAD_UNLOADED_POS = 1;
    private double FLICK_HAMMER_POS = -1;
    private double LOAD_LOAD_POS = 0;

    private int BASE_INDEXER_POS = 178;

    private double FLICK_HAMMER_TIME = 0.6;
    private double FLICK_START_TIME = 0.6;
    private double LOAD_LOAD_TIME = 0.6;
    private double LOAD_UNLOAD_TIME = 0.6;

    public int shotsRemaining = 0;
    private int index = 3;

    private double TARGET_FLYWHEEL_POWER = 0.5;

    private double MAX_FLYWHEEL_TIME = 4;

    public void init (HardwareMap hwMap) {
        spindexer = hwMap.get(DcMotorEx.class, "spindexifier");
        flickServo = hwMap.get(Servo.class, "shootGate1");
        loadServo = hwMap.get(Servo.class, "shootGate2");
        shootMotor = hwMap.get(DcMotorEx.class, "shootMotor");

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
                if (shotsRemaining > 0) {
                    flickServo.setPosition(FLICK_STARTER_POS);
                    loadServo.setPosition(LOAD_UNLOADED_POS);
                    shootMotor.setPower(TARGET_FLYWHEEL_POWER);

                    stateTimer.reset();
                    flywheelState = FlywheelState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                if (shotsRemaining < 3 || stateTimer.seconds() > MAX_FLYWHEEL_TIME) {
                    loadServo.setPosition(LOAD_UNLOADED_POS);
                    flickServo.setPosition(FLICK_HAMMER_POS);
                    stateTimer.reset();

                    flywheelState = FlywheelState.LOAD;
                }
                break;
            case LOAD:
                if (stateTimer.seconds() > FLICK_HAMMER_TIME) {
                    flickServo.setPosition(FLICK_STARTER_POS);
                    loadServo.setPosition(LOAD_LOAD_POS);
                    stateTimer.reset();
                    flywheelState = FlywheelState.LAUNCH;
                }
                break;
            case LAUNCH:
                if (stateTimer.seconds() > LOAD_LOAD_TIME) {
                    shotsRemaining--;
                    loadServo.setPosition(LOAD_UNLOADED_POS);
                    spinUseLeft();
                    stateTimer.reset();

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
                        index = 3;
                        flywheelState = FlywheelState.IDLE;
                    }
                }
                break;
        }
    }

    public void fireShots(int numberOfShots) {
        if (flywheelState == FlywheelState.IDLE) {
            shotsRemaining = numberOfShots;
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
