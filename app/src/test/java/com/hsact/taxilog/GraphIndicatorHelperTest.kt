package com.hsact.taxilog

import com.hsact.taxilog.ui.cards.GraphIndicatorHelper
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class GraphIndicatorHelperTest {

    @Test
    fun `formatIndicatorValue formats positive values correctly`() {
        val localeRu = Locale.forLanguageTag("ru")
        assertEquals("500", GraphIndicatorHelper.formatIndicatorValue(500.0, localeRu))
        assertEquals("2 тыс", GraphIndicatorHelper.formatIndicatorValue(2000.0, localeRu))
        assertEquals("2,5 тыс", GraphIndicatorHelper.formatIndicatorValue(2500.0, localeRu))
    }

    @Test
    fun `formatIndicatorValue formats negative values correctly`() {
        val localeRu = Locale.forLanguageTag("ru")
        assertEquals("-500", GraphIndicatorHelper.formatIndicatorValue(-500.0, localeRu))
        assertEquals("-2 тыс", GraphIndicatorHelper.formatIndicatorValue(-2000.0, localeRu))
        assertEquals("-2,5 тыс", GraphIndicatorHelper.formatIndicatorValue(-2500.0, localeRu))
    }

    @Test
    fun `buildIndicators returns 5 indicators spanning from min to max`() {
        val indicators = GraphIndicatorHelper.buildIndicators(-100.0, 300.0)
        assertEquals(5, indicators.size)
        assertEquals(300.0, indicators.first(), 0.001)
        assertEquals(-100.0, indicators.last(), 0.001)
    }
}
