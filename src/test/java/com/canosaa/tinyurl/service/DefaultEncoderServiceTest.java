package com.canosaa.tinyurl.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultEncoderServiceTest {

    private final DefaultEncoderService encoderService = new DefaultEncoderService();

    @Test
    void toBase62EncodesSingleCharacterBoundaries() {
        assertThat(encoderService.toBase62(0)).isEqualTo("0");
        assertThat(encoderService.toBase62(9)).isEqualTo("9");
        assertThat(encoderService.toBase62(10)).isEqualTo("A");
        assertThat(encoderService.toBase62(35)).isEqualTo("Z");
        assertThat(encoderService.toBase62(36)).isEqualTo("a");
        assertThat(encoderService.toBase62(61)).isEqualTo("z");
    }

    @Test
    void toBase62EncodesMultipleCharacters() {
        assertThat(encoderService.toBase62(62)).isEqualTo("10");
        assertThat(encoderService.toBase62(63)).isEqualTo("11");
        assertThat(encoderService.toBase62(3843)).isEqualTo("zz");
        assertThat(encoderService.toBase62(3844)).isEqualTo("100");
    }

    @Test
    void fromBase62DecodesSingleAndMultipleCharacters() {
        assertThat(encoderService.fromBase62("0")).isZero();
        assertThat(encoderService.fromBase62("9")).isEqualTo(9);
        assertThat(encoderService.fromBase62("A")).isEqualTo(10);
        assertThat(encoderService.fromBase62("Z")).isEqualTo(35);
        assertThat(encoderService.fromBase62("a")).isEqualTo(36);
        assertThat(encoderService.fromBase62("z")).isEqualTo(61);
        assertThat(encoderService.fromBase62("10")).isEqualTo(62);
        assertThat(encoderService.fromBase62("zz")).isEqualTo(3843);
        assertThat(encoderService.fromBase62("100")).isEqualTo(3844);
    }

    @Test
    void base62RoundTripsRepresentativeValues() {
        long[] values = {0, 1, 61, 62, 1_000_000, 1_000_001, 9_999_999};

        for (long value : values) {
            assertThat(encoderService.fromBase62(encoderService.toBase62(value))).isEqualTo(value);
        }
    }

    @Test
    void toBase16EncodesSingleCharacterBoundaries() {
        assertThat(encoderService.toBase16(0)).isEqualTo("0");
        assertThat(encoderService.toBase16(9)).isEqualTo("9");
        assertThat(encoderService.toBase16(10)).isEqualTo("A");
        assertThat(encoderService.toBase16(15)).isEqualTo("F");
    }

    @Test
    void toBase16EncodesMultipleCharacters() {
        assertThat(encoderService.toBase16(16)).isEqualTo("10");
        assertThat(encoderService.toBase16(31)).isEqualTo("1F");
        assertThat(encoderService.toBase16(255)).isEqualTo("FF");
    }
}
