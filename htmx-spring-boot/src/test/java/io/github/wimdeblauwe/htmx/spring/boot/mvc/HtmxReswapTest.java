package io.github.wimdeblauwe.htmx.spring.boot.mvc;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

public class HtmxReswapTest {

    @Test
    void testFocusScrollFalse() {
        var reswap = HtmxReswap.innerHtml().focusScroll(false);
        assertThat(reswap).hasToString("innerHTML focus-scroll:false");
    }

    @Test
    void testFocusScrollTrue() {
        var reswap = HtmxReswap.innerHtml().focusScroll(true);
        assertThat(reswap).hasToString("innerHTML focus-scroll:true");
    }

    @Test
    void testScrollBottom() {
        var reswap = HtmxReswap.innerHtml().scroll(HtmxReswap.Position.BOTTOM);
        assertThat(reswap).hasToString("innerHTML scroll:bottom");
    }

    @Test
    void testScrollBottomWithTarget() {
        var reswap = HtmxReswap.innerHtml()
                               .scroll(HtmxReswap.Position.BOTTOM)
                               .scrollTarget("#target");

        assertThat(reswap).hasToString("innerHTML scroll:#target:bottom");
    }

    @Test
    void testScrollTop() {
        var reswap = HtmxReswap.innerHtml().scroll(HtmxReswap.Position.TOP);
        assertThat(reswap).hasToString("innerHTML scroll:top");
    }

    @Test
    void testScrollTopWithTarget() {
        var reswap = HtmxReswap.innerHtml()
                               .scroll(HtmxReswap.Position.TOP)
                               .scrollTarget("#target");

        assertThat(reswap).hasToString("innerHTML scroll:#target:top");
    }

    @Test
    void testSettle() {
        var reswap = HtmxReswap.innerHtml().settle(Duration.ofMillis(200));
        assertThat(reswap).hasToString("innerHTML settle:200ms");
    }

    @Test
    void testShowBottom() {
        var reswap = HtmxReswap.innerHtml().show(HtmxReswap.Position.BOTTOM);
        assertThat(reswap).hasToString("innerHTML show:bottom");
    }

    @Test
    void testShowBottomWithTarget() {
        var reswap = HtmxReswap.innerHtml()
                               .show(HtmxReswap.Position.BOTTOM)
                               .showTarget("#target");

        assertThat(reswap).hasToString("innerHTML show:#target:bottom");
    }

    @Test
    void testShowTop() {
        var reswap = HtmxReswap.innerHtml().show(HtmxReswap.Position.TOP);
        assertThat(reswap).hasToString("innerHTML show:top");
    }

    @Test
    void testShowTopWithTarget() {
        var reswap = HtmxReswap.innerHtml()
                               .show(HtmxReswap.Position.TOP)
                               .showTarget("#target");

        assertThat(reswap).hasToString("innerHTML show:#target:top");
    }

    @Test
    void testSwap() {
        var reswap = HtmxReswap.innerHtml().swap(Duration.ofMillis(200));
        assertThat(reswap).hasToString("innerHTML swap:200ms");
    }

    @Test
    void testSwapOptions() {
        assertThat(HtmxReswap.afterBegin()).hasToString("afterbegin");
        assertThat(HtmxReswap.afterEnd()).hasToString("afterend");
        assertThat(HtmxReswap.beforeBegin()).hasToString("beforebegin");
        assertThat(HtmxReswap.beforeEnd()).hasToString("beforeend");
        assertThat(HtmxReswap.delete()).hasToString("delete");
        assertThat(HtmxReswap.none()).hasToString("none");
        assertThat(HtmxReswap.outerHtml()).hasToString("outerHTML");
    }

    @Test
    void testTransition() {
        var reswap = HtmxReswap.innerHtml().transition();
        assertThat(reswap).hasToString("innerHTML transition:true");
    }

}
