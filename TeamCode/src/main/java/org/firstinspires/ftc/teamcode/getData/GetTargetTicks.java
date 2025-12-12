package org.firstinspires.ftc.teamcode.getData;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import java.util.List;


@TeleOp(name = "Get Target Ticks From Spindexifier")
    public class GetTargetTicks extends LinearOpMode {

        private DcMotorEx spindexifier;



        @Override
        public void runOpMode() {
            // Define all motors and servos
            spindexifier = hardwareMap.get(DcMotorEx.class, "spindexifier");
            //spindexifier.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            // Main loop for the motors
            waitForStart();
            while (opModeIsActive()) {
                telemetry.addData("Spindexer Position",spindexifier.getCurrentPosition());
                telemetry.update();
            }
        }
    }
