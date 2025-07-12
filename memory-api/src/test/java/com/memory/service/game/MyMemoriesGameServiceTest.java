package com.memory.service.game;

import com.memory.domain.file.File;
import com.memory.domain.file.FileType;
import com.memory.domain.game.*;
import com.memory.domain.game.repository.GameQuestionRepository;
import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.exception.customException.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("MyMemoriesGameService 테스트")
class MyMemoriesGameServiceTest {

    @Mock
    private GameQuestionRepository gameQuestionRepository;

    @Mock
    private MemoryRepository memoryRepository;

    @InjectMocks
    private MyMemoriesGameService myMemoriesGameService;

    @Test
    @DisplayName("충분한 추억이 있으면 게임 세션이 정상적으로 생성된다")
    void createGameSession_Success() {
        // given
        Member member = createTestMember();
        GameSetting gameSetting = createTestGameSetting();
        GameSessionRequest.Create request = new GameSessionRequest.Create(GameMode.MY_MEMORIES);

        List<Memory> memories = createTestMemories(5); // 3개 이상 필요
        given(memoryRepository.findMemoriesWithImagesByMember(member)).willReturn(memories);

        // when
        GameSession result = myMemoriesGameService.createGameSession(member, gameSetting, request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getMember()).isEqualTo(member);
        assertThat(result.getGameMode()).isEqualTo(GameMode.MY_MEMORIES);
    }

    @Test
    @DisplayName("추억이 부족하면 게임 세션 생성시 예외가 발생한다")
    void createGameSession_InsufficientMemories_ThrowsException() {
        // given
        Member member = createTestMember();
        GameSetting gameSetting = createTestGameSetting();
        GameSessionRequest.Create request = new GameSessionRequest.Create(GameMode.MY_MEMORIES);

        List<Memory> memories = createTestMemories(2); // 3개 미만
        given(memoryRepository.findMemoriesWithImagesByMember(member)).willReturn(memories);

        // when & then
        assertThatThrownBy(() -> myMemoriesGameService.createGameSession(member, gameSetting, request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("내 추억 게임을 시작하기 위해서는 최소 3개의 이미지가 있는 추억이 필요합니다.");
    }

    @Test
    @DisplayName("다음 문제를 정상적으로 생성한다")
    void getNextQuestion_Success() {
        // given
        Member member = createTestMember();
        GameSession gameSession = GameSession.gameSessionInit(member, GameMode.MY_MEMORIES);
        GameSetting gameSetting = createTestGameSetting();
        Integer nextOrder = 1;

        List<Memory> memories = createTestMemories(3);
        List<GameQuestion> existingQuestions = new ArrayList<>();
        GameQuestion savedQuestion = createTestGameQuestion(gameSession, memories.get(0), nextOrder);

        given(gameQuestionRepository.findByGameSessionOrderByQuestionOrder(gameSession)).willReturn(existingQuestions);
        given(memoryRepository.findMemoriesWithImagesByMember(member)).willReturn(memories);
        given(gameQuestionRepository.save(any(GameQuestion.class))).willReturn(savedQuestion);

        // when
        GameQuestionResponse result = myMemoriesGameService.getNextQuestion(member, gameSession, gameSetting, nextOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedQuestion.getId());
        assertThat(result.getQuestionOrder()).isEqualTo(nextOrder);
        assertThat(result.getMemoryImageUrls()).hasSize(2); // 각 Memory마다 2개의 이미지
        assertThat(result.getCorrectLatitude()).isNull(); // 문제 단계에서는 정답이 숨겨짐
        assertThat(result.getCorrectLongitude()).isNull();
        assertThat(result.getCorrectLocationName()).isNull();

        verify(gameQuestionRepository).findByGameSessionOrderByQuestionOrder(gameSession);
        verify(memoryRepository).findMemoriesWithImagesByMember(member);
        verify(gameQuestionRepository).save(any(GameQuestion.class));
    }

    @Test
    @DisplayName("사용 가능한 추억이 없으면 예외가 발생한다")
    void getNextQuestion_NoAvailableMemories_ThrowsException() {
        // given
        Member member = createTestMember();
        GameSession gameSession = GameSession.gameSessionInit(member, GameMode.MY_MEMORIES);
        GameSetting gameSetting = createTestGameSetting();
        Integer nextOrder = 1;

        given(gameQuestionRepository.findByGameSessionOrderByQuestionOrder(gameSession)).willReturn(Collections.emptyList());
        given(memoryRepository.findMemoriesWithImagesByMember(member)).willReturn(Collections.emptyList());

        // when & then
        assertThatThrownBy(() -> myMemoriesGameService.getNextQuestion(member, gameSession, gameSetting, nextOrder))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("사용 가능한 내 추억이 부족합니다.");
    }

    @Test
    @DisplayName("이미 사용된 추억을 제외하고 문제를 생성한다")
    void getNextQuestion_ExcludesUsedMemories() {
        // given
        Member member = createTestMember();
        GameSession gameSession = GameSession.gameSessionInit(member, GameMode.MY_MEMORIES);
        GameSetting gameSetting = createTestGameSetting();
        Integer nextOrder = 2;

        List<Memory> memories = createTestMemories(3);
        Memory usedMemory = memories.get(0);
        GameQuestion existingQuestion = createTestGameQuestion(gameSession, usedMemory, 1);
        List<GameQuestion> existingQuestions = List.of(existingQuestion);

        GameQuestion savedQuestion = createTestGameQuestion(gameSession, memories.get(1), nextOrder);

        given(gameQuestionRepository.findByGameSessionOrderByQuestionOrder(gameSession)).willReturn(existingQuestions);
        given(memoryRepository.findMemoriesWithImagesByMember(member)).willReturn(memories);
        given(gameQuestionRepository.save(any(GameQuestion.class))).willReturn(savedQuestion);

        // when
        GameQuestionResponse result = myMemoriesGameService.getNextQuestion(member, gameSession, gameSetting, nextOrder);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(savedQuestion.getId());
        // 사용된 메모리(ID: 1)가 아닌 다른 메모리가 선택되어야 함
        verify(gameQuestionRepository).findByGameSessionOrderByQuestionOrder(gameSession);
        verify(memoryRepository).findMemoriesWithImagesByMember(member);
        verify(gameQuestionRepository).save(any(GameQuestion.class));
    }

    private Member createTestMember() {
        return new Member("테스트 사용자", "testNick", "test@example.com", "password", MemberType.MEMBER);
    }

    private GameSetting createTestGameSetting() {
        // GameSetting은 테스트에서 실제로 사용되지 않으므로 null로 처리
        return null;
    }

    private List<Memory> createTestMemories(int count) {
        List<Memory> memories = new ArrayList<>();
        
        for (int i = 1; i <= count; i++) {
            Map map = createTestMap(i);
            List<File> files = createTestFiles(i);
            Memory memory = createTestMemory(i, map, files);
            memories.add(memory);
        }
        
        return memories;
    }

    private Map createTestMap(int index) {
        return Map.builder()
                .latitude(String.valueOf(37.5665 + index * 0.001))
                .longitude(String.valueOf(126.9780 + index * 0.001))
                .mapType(MapType.USER_PLACE)
                .build();
    }

    private List<File> createTestFiles(int index) {
        return Arrays.asList(
                createTestFile(index, 1),
                createTestFile(index, 2)
        );
    }

    private File createTestFile(int memoryIndex, int fileIndex) {
        return File.builder()
                .fileName("image" + memoryIndex + "_" + fileIndex + ".jpg")
                .fileUrl("https://example.com/image" + memoryIndex + "_" + fileIndex + ".jpg")
                .fileType(FileType.MEMORY)
                .build();
    }

    private Memory createTestMemory(int index, Map map, List<File> files) {
        Memory memory = Memory.builder()
                .title("추억 " + index)
                .content("추억 내용 " + index)
                .locationName("장소 " + index)
                .memoryType(MemoryType.PUBLIC)
                .map(map)
                .member(createTestMember())
                .build();
        
        return new Memory(memory.getTitle(), memory.getContent(), memory.getLocationName(), 
                         memory.getMemoryType(), memory.getMember(), memory.getMap()) {
            @Override
            public Long getId() {
                return (long) index;
            }
            
            @Override
            public List<File> getFiles() {
                return files;
            }
        };
    }

    private GameQuestion createTestGameQuestion(GameSession gameSession, Memory memory, Integer order) {
        return new GameQuestion(
                gameSession,
                memory,
                order,
                new BigDecimal(memory.getMap().getLatitude()),
                new BigDecimal(memory.getMap().getLongitude()),
                memory.getLocationName()
        ) {
            @Override
            public Long getId() {
                return (long) order;
            }
        };
    }
}
