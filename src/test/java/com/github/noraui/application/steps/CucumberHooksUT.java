package com.github.noraui.application.steps;

import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.github.noraui.application.steps.CucumberHooks;
import com.github.noraui.data.DataInputProvider;
import com.github.noraui.data.excel.ExcelDataProvider;
import com.github.noraui.utils.Context;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Context.class, DateTime.class })
@PowerMockIgnore("javax.net.ssl.*")
public class CucumberHooksUT {

    DataInputProvider mockDataInputProvider;

    @Before
    public void setUp() throws InterruptedException {
        DateTime now = DateTime.now();
        // prepare mock
        mockDataInputProvider = Mockito.mock(ExcelDataProvider.class);
        PowerMockito.mockStatic(Context.class);
        when(Context.getDataInputProvider()).thenReturn(mockDataInputProvider);
        when(Context.getStartCurrentScenario()).thenReturn(now);
        DateTimeUtils.setCurrentMillisFixed(now.plusSeconds(5).getMillis());
    }

    @Test
    public void testGetRemainingTime1() {
        when(mockDataInputProvider.getNbGherkinExample()).thenReturn(10);
        when(Context.getCurrentScenarioData()).thenReturn(1);
        Assert.assertEquals(45, CucumberHooks.getRemainingTime());
    }

    @Test
    public void testGetRemainingTime2() throws InterruptedException {
        when(mockDataInputProvider.getNbGherkinExample()).thenReturn(10);
        when(Context.getCurrentScenarioData()).thenReturn(2);
        Assert.assertEquals(20, CucumberHooks.getRemainingTime());
    }

    @Test
    public void testGetRemainingTime1only() throws InterruptedException {
        when(mockDataInputProvider.getNbGherkinExample()).thenReturn(1);
        when(Context.getCurrentScenarioData()).thenReturn(1);
        Assert.assertEquals(0, CucumberHooks.getRemainingTime());
    }
}
