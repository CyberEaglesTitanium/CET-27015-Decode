package org.firstinspires.ftc.teamcode.autonomousOpModes;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous (name = "BlueAuto")
public class BlueLineMove extends LinearOpMode {
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor backLeft;
    private DcMotor backRight;

    private DcMotorEx shootMotor;

    private CRServo shootGate1;
    private CRServo shootGate2;

    void launchCodes() {
        shootMotor.setPower(0.8);
        sleep(1500);
        shootGate1.setPower(-1);
        shootGate2.setPower(1);
        sleep(1500);
        shootMotor.setPower(0);
        shootGate1.setPower(0);
        shootGate2.setPower(0);
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
        frontLeft.setPower(0.5);
        frontRight.setPower(-0.5);
        backLeft.setPower(0.5);
        backRight.setPower(-0.5);
        sleep(800);
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    void testThing() {
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
    }

    public void runOpMode() {
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");
        shootMotor = hardwareMap.get(DcMotorEx.class, "shootMotor");
        shootGate1 = hardwareMap.get(CRServo.class, "shootGate1");
        shootGate2 = hardwareMap.get(CRServo.class, "shootGate2");

        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        testThing();
    }
}
