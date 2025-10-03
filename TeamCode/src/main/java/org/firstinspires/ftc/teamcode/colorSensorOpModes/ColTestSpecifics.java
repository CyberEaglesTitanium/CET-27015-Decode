package org.firstinspires.ftc.teamcode.colorSensorOpModes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
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

        while (colors.blue >= 100 && colors.red <= 162) {
            telemetry.addLine("PURPLE FOUND");
            telemetry.update();
        }
        while (colors.green == 255) {
            telemetry.addLine("GREEN FOUND");
            telemetry.update();
        }
    }
}
