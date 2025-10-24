package org.firstinspires.ftc.teamcode.colorSensorOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;


@TeleOp
public class ColTestSpecifics extends LinearOpMode {
    private NormalizedColorSensor colsense;

    @Override
    public void runOpMode() {
        colsense = hardwareMap.get(NormalizedColorSensor.class, "colsense");
        waitForStart();
        NormalizedRGBA colors = colsense.getNormalizedColors();

        while (opModeIsActive()) {
            while (colors.blue >= 0.071 && colors.red >= 0.065 && colors.green >= 0.106) {
                telemetry.clear();
                telemetry.addLine("PURPLE FOUND");
                telemetry.update();
            }
            while (colors.green <= 0.160 && colors.red <= 0.025 && colors.blue <= 0.060) {
                telemetry.clear();
                telemetry.addLine("GREEN FOUND");
                telemetry.update();
            }
        }

    }
}
