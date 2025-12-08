package org.firstinspires.ftc.teamcode.autonomousOpModes;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

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
    private CRServo shootGate2;

    void flick() {
        shootGate1.setPosition(1);
        sleep(500);
        shootGate1.setPosition(-1);
    }

    void spinIndex() {
        spindexifier.setTargetPosition((752 / 3) * index);
    }

    void spinUseRight() {
        if (index == 3) {
            index = 1;
        } else {
            index += 1;
        }
        spinIndex();
        spindexifier.setPower(1);
    }

    void launchCodes() {
        shootMotor.setPower(0.7);
        sleep(1000);
        spinUseRight();
        sleep(750);
        flick();
        sleep(500);
        shootMotor.setPower(0);
        spindexifier.setPower(0);

//        sleep(250);
//        shootGate1.setPower(1);
//        shootGate2.setPower(1);
//        sleep(100);
//        shootMotor.setPower(1);
//        sleep(600);
//        shootMotor.setPower(0);
//        sleep(250);
//        shootGate1.setPower(1);
//        shootGate2.setPower(1);
//        sleep(100);
//        shootMotor.setPower(1);
//        sleep(600);
//        shootMotor.setPower(0);
    }

    void turnDangIt() {
        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
        sleep(700);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }
    void turnDangItReverse() {
        frontLeft.setPower(-0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(-0.5);
        backRight.setPower(0.5);
        sleep(700);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    void testThing() {
        sleep(500);
        frontLeft.setPower(0.5);
        frontRight.setPower(0.5);
        backLeft.setPower(0.5);
        backRight.setPower(0.5);
        sleep(1600);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        sleep(250);
        turnDangIt();
        launchCodes();
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
        shootGate2 = hardwareMap.get(CRServo.class, "shootGate2");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        testThing();
    }
}
