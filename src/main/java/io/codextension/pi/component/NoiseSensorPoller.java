package io.codextension.pi.component;

import com.pi4j.wiringpi.Gpio;
import com.pi4j.wiringpi.GpioUtil;
import io.codextension.pi.model.Dht;
import io.codextension.pi.repository.DhtRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

/**
 * Created by elie on 09.03.17.
 */
@Component
public class NoiseSensorPoller {
    private static final Logger LOG = LoggerFactory.getLogger(NoiseSensorPoller.class);

    @Autowired
    private AnalogSensorReader analogSensorReader;



    @Scheduled(fixedRate = 100)
    public void pollTemperatureAndHumidity() {
        try {
            Double value = analogSensorReader.getNoiseValue();
            if (value > 0) {
                Double voltage = (value * 5) / 1024.0;
                LOG.debug("Noise value is " + value + ", voltage = " + voltage);
                if (voltage < 1.2) {
                    Gpio.digitalWrite(5, Gpio.LOW);
                    Gpio.digitalWrite(6, Gpio.LOW);
                    Gpio.digitalWrite(13, Gpio.HIGH);
                } else if (voltage < 2.1) {
                    Gpio.digitalWrite(5, Gpio.LOW);
                    Gpio.digitalWrite(6, Gpio.HIGH);
                    Gpio.digitalWrite(13, Gpio.LOW);
                } else if (voltage > 3) {
                    Gpio.digitalWrite(5, Gpio.HIGH);
                    Gpio.digitalWrite(6, Gpio.LOW);
                    Gpio.digitalWrite(13, Gpio.LOW);
                } else {
                    Gpio.digitalWrite(5, Gpio.LOW);
                    Gpio.digitalWrite(6, Gpio.LOW);
                    Gpio.digitalWrite(13, Gpio.HIGH);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}