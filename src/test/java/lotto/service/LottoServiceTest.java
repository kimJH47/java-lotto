package lotto.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lotto.domain.Lotto;
import lotto.domain.LottoRepository;
import lotto.domain.Money;
import lotto.type.Prize;
import lotto.view.LottoDto;
import lotto.view.LottoInputDto;
import lotto.view.PrizeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class LottoServiceTest {

    private LottoService lottoService;
    private LottoRepository lottoRepository;

    @BeforeEach
    void init() {
        lottoRepository = new LottoRepository();
        lottoService = new LottoService(lottoRepository);
    }

    @Test
    @DisplayName("Money 10000 을 받으면 로또 10개가 생성되고 repo 에 저장이 되고 랜덤으로 생성된 정수리스트 10개가 반환")
    public void 로또생성_테스트() {
        //given
        //when
        LottoDto lottos = lottoService.createLottos(Money.of(10000L));
        //then
        assertThat(lottos.getLotteries().size()).isEqualTo(10);
    }

    @DisplayName("randomNumbers 을 인자로 받아 생성된 로또 1개의 당첨결과를 테스트 한다.")
    @ParameterizedTest(name = "{index} => {0} 으로 생성된 로또는 {1} 가 당첨결과로 나와야하고 갯수는 {2}개 이다 ")
    @MethodSource("provideRandomNumbersForCalcPrize")
    public void 로또1개_당첨결과_테스트(List<Integer> randomNumbers, Prize prize, long expected) {
        //given
        List<Integer> winningNumbers = List.of(1, 5, 15, 21, 40, 41);
        int bonus = 13;
        lottoRepository.save(new Lotto(randomNumbers));

        //when
        LottoInputDto lottoInputDto = new LottoInputDto(winningNumbers, bonus);
        lottoInputDto.setMoney(Money.of(1000L));
        PrizeDto prizeDto = lottoService.calcWinningResult(lottoInputDto);

        //then
        Map<Prize, Long> prizeLongMap = prizeDto.groupByPrizes();
        assertThat(prizeLongMap.get(prize)).isEqualTo(expected);

    }

    private static Stream<Arguments> provideRandomNumbersForCalcPrize() { // argument source method
        return Stream.of(
                Arguments.of(List.of(1, 5, 15, 18, 20, 39), Prize.THREE, 1L),
                Arguments.of(List.of(1, 5, 15, 18, 21, 39), Prize.FOUR, 1L),
                Arguments.of(List.of(1, 5, 15, 18, 21, 40), Prize.FIVE, 1L)
        );
    }
}