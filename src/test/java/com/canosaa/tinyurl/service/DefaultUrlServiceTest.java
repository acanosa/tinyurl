package com.canosaa.tinyurl.service;

import com.canosaa.tinyurl.model.Url;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUrlServiceTest {

    private static final long OFFSET = 1_000_000;

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private EncoderService encoderService;

    @InjectMocks
    private DefaultUrlService urlService;

    @Test
    void shortenUrlReturnsExistingShortUrlWhenUrlAlreadyExists() {
        var originalUrl = "https://example.com/articles/1";
        var storedUrl = Url.builder()
                .id(10)
                .url(originalUrl)
                .shortUrl("abc123")
                .build();

        when(urlRepository.existsByUrl(originalUrl)).thenReturn(true);
        when(urlRepository.findByUrl(originalUrl)).thenReturn(Optional.of(storedUrl));

        var shortenedUrl = urlService.shortenUrl(originalUrl);

        assertThat(shortenedUrl).isEqualTo("abc123");
        verify(urlRepository).existsByUrl(originalUrl);
        verify(urlRepository).findByUrl(originalUrl);
        verify(urlRepository, never()).save(any(Url.class));
        verifyNoInteractions(encoderService);
    }

    @Test
    void shortenUrlSavesNewUrlAndReturnsEncodedValue() {
        var originalUrl = "https://example.com/articles/2";
        var savedUrl = Url.builder()
                .id(42)
                .url(originalUrl)
                .build();

        when(urlRepository.existsByUrl(originalUrl)).thenReturn(false);
        when(urlRepository.save(any(Url.class))).thenReturn(savedUrl);
        when(encoderService.toBase62(OFFSET + savedUrl.getId())).thenReturn("4C9e");

        var shortenedUrl = urlService.shortenUrl(originalUrl);

        var urlCaptor = ArgumentCaptor.forClass(Url.class);
        assertThat(shortenedUrl).isEqualTo("4C9e");
        assertThat(savedUrl.getShortUrl()).isEqualTo("4C9e");
        verify(urlRepository).save(urlCaptor.capture());
        assertThat(urlCaptor.getValue().getUrl()).isEqualTo(originalUrl);
        verify(urlRepository, never()).findByUrl(originalUrl);
        verify(encoderService).toBase62(OFFSET + savedUrl.getId());
    }

    @Test
    void getOriginalUrlReturnsUrlWhenShortUrlExists() {
        var shortUrl = "4C9e";
        var storedUrl = Url.builder()
                .id(42)
                .url("https://example.com/articles/2")
                .shortUrl(shortUrl)
                .build();

        when(encoderService.fromBase62(shortUrl)).thenReturn(OFFSET + storedUrl.getId());
        when(urlRepository.findById(storedUrl.getId())).thenReturn(Optional.of(storedUrl));

        var originalUrl = urlService.getOriginalUrl(shortUrl);

        assertThat(originalUrl).isEqualTo("https://example.com/articles/2");
        verify(encoderService).fromBase62(shortUrl);
        verify(urlRepository).findById(storedUrl.getId());
    }

    @Test
    void getOriginalUrlThrowsWhenShortUrlDoesNotExist() {
        var shortUrl = "missing";
        var decodedId = OFFSET + 99;

        when(encoderService.fromBase62(shortUrl)).thenReturn(decodedId);
        when(urlRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> urlService.getOriginalUrl(shortUrl))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not found");

        verify(encoderService).fromBase62(shortUrl);
        verify(urlRepository).findById(99L);
    }
}
