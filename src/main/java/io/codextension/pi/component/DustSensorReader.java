package io.codextension.pi.component;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import io.codextension.pi.model.Dust;

import java.io.IOException;

/**
 * Created by elie on 16.04.17.
 */
public class DustSensorReader {

    /**
     * For info
     * 3000 +     = VERY POOR
     * 1050-3000  = POOR
     * 300-1050   = FAIR
     * 150-300    = GOOD
     * 75-150     = VERY GOOD
     * 0-75       = EXCELLENT
     *
     * @return
     * @throws IOException
     */
    public Dust getValue() throws IOException {
        MCP3008GpioProvider provider = new MCP3008GpioProvider(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE, false);

        provider.export(MCP3008Pin.CH0, PinMode.ANALOG_INPUT);
        Gpio.pinMode(12, Gpio.OUTPUT);

        Gpio.digitalWrite(12, Gpio.LOW);
        Gpio.delayMicroseconds(280);
        double inValue = provider.getImmediateValue(MCP3008Pin.CH0);
        double voltage = ((inValue * 3.3) / 1024.0);
        double dustDensity = (voltage * 0.17 - 0.1) * 1000;
        Gpio.delayMicroseconds(40);
        Gpio.digitalWrite(12, Gpio.HIGH);

        GpioUtil.unexport(12);
        provider.unexport(MCP3008Pin.CH0);
        Dust dust = new Dust(inValue, voltage, dustDensity);
        return dust;
    }
}