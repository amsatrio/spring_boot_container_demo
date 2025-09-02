package io.github.amsatrio.spring_boot_container_demo.health;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class HealthServiceTest {
    @InjectMocks
    private HealthService healthService;
    @Mock
    private HealthRepository healthRepository;

    @Test
    public void getStatus(){
        String expected = "ok";
        when(healthRepository.getStatus()).thenReturn(expected);

        String actual = healthService.status();

        assertEquals(expected, actual);
    }
}
