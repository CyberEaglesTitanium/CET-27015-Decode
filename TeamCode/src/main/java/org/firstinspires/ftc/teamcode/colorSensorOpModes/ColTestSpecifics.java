package org.firstinspires.ftc.teamcode.colorSensorOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;

@TeleOp
public class ColTestSpecifics extends LinearOpMode {
    private ColorSensor colsense;

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(ColorSensor.class, "colsense");
        waitForStart();

        while (opModeIsActive()) {
            while (colsense.blue() <= 113 && colsense.red() <= 90 && colsense.green() >= 76) {
                telemetry.clear();
                telemetry.addLine("PURPLE FOUND");
                telemetry.update();
            }
            while (colsense.green() <= 394 && colsense.red() <= 128 && colsense.blue() <= 243) {
                telemetry.clear();
                telemetry.addLine("GREEN FOUND");
                telemetry.update();
            }
        }

    }
}
