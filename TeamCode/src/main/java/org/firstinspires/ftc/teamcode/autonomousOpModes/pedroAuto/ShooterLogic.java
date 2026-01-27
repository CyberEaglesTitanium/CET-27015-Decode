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
    private double LOAD_OPEN_POS = 1;
    private double FLICK_HAMMER_POS = -1;
    private double LOAD_CLOSE_POS = 0;

    private int BASE_INDEXER_POS = -178;

    private double FLICK_OPEN_TIME = 1.2;
    private double FLICK_CLOSE_TIME = 1.2;
    private double LOAD_OPEN_TIME = 1.2;
    private double LOAD_CLOSE_TIME = 1.2;

    public int shotsRemaining = 0;
    private int index = shotsRemaining;

    private double flywheelVelocity = 0;
    private double MIN_FLYWHEEL_RPM = 2700;
    private double TARGET_FLYWHEEL_RPM = 0.792;

    private double MAX_FLYWHEEL_TIME = 5.5;

    public void init (HardwareMap hwMap) {
        spindexer = hwMap.get(DcMotorEx.class, "spindexifier");
        flickServo = hwMap.get(Servo.class, "shootGate1");
        loadServo = hwMap.get(Servo.class, "shootGate2");
        shootMotor = hwMap.get(DcMotorEx.class, "shootMotor");

        spindexer.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        flywheelState = FlywheelState.IDLE;

        shootMotor.setPower(0);
        flickServo.setPosition(FLICK_STARTER_POS);
        loadServo.setPosition(LOAD_OPEN_POS);
    }

    public void update() {
        switch (flywheelState) {
            case IDLE:
                if (shotsRemaining > 0) {
                    flickServo.setPosition(FLICK_HAMMER_POS);
                    loadServo.setPosition(LOAD_OPEN_POS);
                    shootMotor.setPower(0);

                    stateTimer.reset();
                    flywheelState = FlywheelState.SPIN_UP;
                }
                break;
            case SPIN_UP:
                if (flywheelVelocity > MIN_FLYWHEEL_RPM || stateTimer.seconds() > MAX_FLYWHEEL_TIME) {
                    loadServo.setPosition(LOAD_OPEN_POS);
                    shootMotor.setPower(TARGET_FLYWHEEL_RPM);
                    stateTimer.reset();

                    flywheelState = FlywheelState.LOAD;
                }
                break;
            case LOAD:
                if (stateTimer.seconds() > FLICK_OPEN_TIME) {
                    flickServo.setPosition(FLICK_STARTER_POS);
                    stateTimer.reset();
                    if (stateTimer.seconds() > 2.5) {
                        flywheelState = FlywheelState.LAUNCH;
                    }
                } else {
                    flickServo.setPosition(FLICK_HAMMER_POS);
                }
                break;
            case LAUNCH:
                stateTimer.reset();
                if (stateTimer.seconds() > LOAD_OPEN_TIME) {
                    shotsRemaining--;
                    loadServo.setPosition(LOAD_CLOSE_POS);
                    spinUseLeft();
                    stateTimer.reset();

                    flywheelState = FlywheelState.RESET;
                } else {
                    loadServo.setPosition(LOAD_OPEN_POS);
                }
                break;
            case RESET:
                if (stateTimer.seconds() > LOAD_CLOSE_TIME) {
                    if (shotsRemaining > 0) {
                        stateTimer.reset();

                        flywheelState = FlywheelState.LOAD;
                    } else {
                        shootMotor.setPower(0);
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

    void spinUseRight() {
        index += 1;
//            if (index == 4) {
//                index = 1;
//            }
        spindexer.setTargetPosition(BASE_INDEXER_POS * index);
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }
    void spinUseLeft() {
        index -= 1;
//            if (index == 0) {
//                index = 3;
//            }
        spindexer.setTargetPosition(BASE_INDEXER_POS * index);
        spindexer.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexer.setPower(0.3);
    }
}
