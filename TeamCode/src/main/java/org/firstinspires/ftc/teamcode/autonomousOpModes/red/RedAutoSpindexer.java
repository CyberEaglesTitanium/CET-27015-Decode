package org.firstinspires.ftc.teamcode.autonomousOpModes.red;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
@Autonomous (name = "RedAutoSpindexed")
public class RedAutoSpindexer extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private DcMotorEx shootMotor;

    private DcMotorEx spindexifier;

    int index;

    private Servo shootGate1;
    private Servo shootGate2;

    void flick() {
        shootGate1.setPosition(-1);
        sleep(500);
        shootGate1.setPosition(0.5);
    }

    void spinIndex() {
        spindexifier.setTargetPosition(178 * index);
    }

    void spinUseRight() {
        index += 1;
        spinIndex();
        spindexifier.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        spindexifier.setPower(1);
    }

    void launchCodes() {
        shootMotor.setPower(0.6);
        sleep(3000);
        shootGate2.setPosition(0);
        sleep(500);
        shootGate2.setPosition(1);
        sleep(1000);
        flick();
        sleep(1000);
        shootGate2.setPosition(0);
        sleep(500);
        shootGate2.setPosition(1);
        spinUseRight();
        sleep(1000);
        flick();
        sleep(1000);
        shootGate2.setPosition(0);
        sleep(1000);
        shootMotor.setPower(0);
        shootGate2.setPosition(1);
        spindexifier.setPower(0);
        sleep(2500);
    }

    // Right
    void turnDangIt() {
        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
        sleep(200);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    // Left
    void turnDangItReverse() {
        frontLeft.setPower(0.5);
        frontRight.setPower(-0.5);
        backLeft.setPower(0.5);
        backRight.setPower(-0.5);
        sleep(250);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    void strafeLeft() {
        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(-0.5);
        sleep(1000);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    void strafeRight() {
        frontLeft.setPower(0.5);
        frontRight.setPower(-0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
        sleep(1000);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    void testThing() {
        spindexifier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sleep(500);
        frontLeft.setPower(0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(0.5);
        sleep(2000);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        sleep(250);
        turnDangItReverse();
        launchCodes();
        sleep(100);
        strafeRight();
//        sleep(250);
//        turnDangItReverse();
//        frontLeft.setPower(-0.5);
//        frontRight.setPower(-0.5);
//        backLeft.setPower(-0.5);
//        backRight.setPower(-0.5);
//        sleep(1600);
//        frontLeft.setPower(0);
//        frontRight.setPower(0);
//        backLeft.setPower(0);
//        backRight.setPower(0);
    }

    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");
        spindexifier = hardwareMap.get(DcMotorEx.class, "spindexifier");
        shootGate1 = hardwareMap.get(Servo.class, "shootGate1");
        shootGate2 = hardwareMap.get(Servo.class, "shootGate2");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        testThing();
    }
}
