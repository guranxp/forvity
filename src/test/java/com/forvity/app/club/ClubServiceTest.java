package com.forvity.app.club;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClubServiceTest {

    @Mock
    private ClubRepository clubRepository;

    private ClubService clubService;

    @BeforeEach
    void setUp() {
        clubService = new ClubService(clubRepository, new SimpleMeterRegistry());
    }

    @Test
    void shouldSaveClubWhenValidInput() {
        when(clubRepository.existsBySlug("fc-stockholm")).thenReturn(false);
        when(clubRepository.save(any(Club.class))).thenAnswer(i -> i.getArgument(0));

        final var club = clubService.create("FC Stockholm", "fc-stockholm");

        assertThat(club.getName()).isEqualTo("FC Stockholm");
        assertThat(club.getSlug()).isEqualTo("fc-stockholm");
    }

    @Test
    void shouldThrowWhenSlugAlreadyInUse() {
        when(clubRepository.existsBySlug("fc-stockholm")).thenReturn(true);

        assertThatThrownBy(() -> clubService.create("FC Stockholm", "fc-stockholm"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Slug already in use");

        verify(clubRepository, never()).save(any());
    }
}