package lotto.service;

import camp.nextstep.edu.missionutils.Randoms;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lotto.domain.Lotto;
import lotto.domain.LottoRepository;
import lotto.type.Prize;

public class LottoService {
    private final LottoRepository lottoRepository;

    public LottoService(LottoRepository lottoRepository) {
        this.lottoRepository = lottoRepository;
    }

    public List<List<Integer>> createLottos(int money) {
        List<List<Integer>> numbers = new ArrayList<>();
        for (int i = 0; i < money / 1000; i++) {
            List<Integer> randomNumbers = Randoms.pickUniqueNumbersInRange(1, 45, 6);
            Lotto lotto = new Lotto(randomNumbers);
            numbers.add(lottoRepository.save(lotto));
        }
        return numbers;
    }

    private Double calcYield(List<Prize> prizes, int money) {
        Long prizeMoney = prizes.stream()
                .map(Prize::getPrizeMoney)
                .reduce(Long::sum)
                .orElse(1L);
        return (double) money / prizeMoney * 100;

    }

    private List<Prize> confirmWinning(List<Integer> winningNumbers, Integer bonusNumber) {
        List<Lotto> lottos = lottoRepository.findAll();
        return lottos.stream()
                .map(lotto -> lotto.compareWinningNumbers(winningNumbers, bonusNumber))
                .filter(prize -> !Prize.NOTING.equals(prize))
                .collect(Collectors.toList());

    }

}
