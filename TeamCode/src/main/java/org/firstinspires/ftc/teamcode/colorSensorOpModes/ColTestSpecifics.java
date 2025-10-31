package org.firstinspires.ftc.teamcode.colorSensorOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class ColTestSpecifics extends LinearOpMode {
    private ColorSensor colsense;
    private Servo sweepingSweeper;

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(ColorSensor.class, "colsense");
        sweepingSweeper = hardwareMap.get(Servo.class, "sweepingSweeper");
        waitForStart();

        while (opModeIsActive()) {
            while (colsense.green() < colsense.red()) {
                telemetry.clear();
                telemetry.addLine("PURPLE FOUND");
                sweepingSweeper.setPosition(1);
                telemetry.update();
            }
            while (colsense.green() > colsense.red()) {
                telemetry.clear();
                telemetry.addLine("GREEN FOUND");
                sweepingSweeper.setPosition(-1);
                telemetry.update();
            }
        }

    }
}
