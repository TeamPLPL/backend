package com.kosa.backend.funding.support.service;

import com.kosa.backend.funding.project.entity.Funding;
import com.kosa.backend.funding.project.repository.FundingRepository;
import com.kosa.backend.funding.support.dto.SupporterBoardDTO;
import com.kosa.backend.funding.support.entity.SupporterBoard;
import com.kosa.backend.funding.support.repository.FundingSupportRepository;
import com.kosa.backend.funding.support.repository.SupporterBoardRepository;
import com.kosa.backend.user.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupporterBoardService {

    private final SupporterBoardRepository supporterBoardRepository;
    private final FundingRepository fundingRepository; // Funding 엔티티 접근
    private final FundingSupportRepository fundingSupportRepository;

    public SupporterBoardService(
            SupporterBoardRepository supporterBoardRepository,
            FundingRepository fundingRepository,
            FundingSupportRepository fundingSupportRepository
    ) {
        this.supporterBoardRepository = supporterBoardRepository;
        this.fundingRepository = fundingRepository;
        this.fundingSupportRepository = fundingSupportRepository;
    }

    // Create
    public SupporterBoard addSupporterBoard(int fundingId, SupporterBoardDTO dto, User user) {
        // 게시글 작성 권한 확인
        boolean isParticipated = fundingSupportRepository.existsByFundingIdAndUserId(fundingId, user.getId());
        if (!isParticipated) {
            throw new RuntimeException("펀딩에 참여하지 않은 사용자는 게시글을 작성할 수 없습니다.");
        }

        SupporterBoard board = new SupporterBoard();

        board.setBoardCategory(dto.getBoardCategory());
        board.setBoardContent(dto.getBoardContent());
        board.setBoardDate(dto.getBoardDate() != null ? dto.getBoardDate() : LocalDateTime.now()); // 기본값 설정
        board.setUser(user);
        board.setFunding(fundingRepository.findById(fundingId)
                .orElseThrow(() -> new RuntimeException("Funding not found")));

        return supporterBoardRepository.save(board);
    }

    // Read (by ID)
    public Optional<SupporterBoard> getSupporterBoardById(int id) {
        return supporterBoardRepository.findById(id);
    }

    // Read (all by funding ID)
    public List<SupporterBoardDTO> getSupporterBoardsByFundingId(int fundingId) {
        List<SupporterBoard> boards = supporterBoardRepository.findByFundingId(fundingId);

        return boards.stream().map(board -> {
            SupporterBoardDTO dto = new SupporterBoardDTO();
            dto.setId(board.getId());
            dto.setBoardCategory(board.getBoardCategory());
            dto.setBoardContent(board.getBoardContent());
            dto.setBoardDate(board.getBoardDate());
            dto.setUserId(board.getUser().getId());
            dto.setUserNick(board.getUser().getUserNick());
            dto.setFundingId(board.getFunding().getId());  // Funding의 ID만 포함
            return dto;
        }).collect(Collectors.toList());
    }

    // Delete
    public void deleteSupporterBoardById(int id) {
        supporterBoardRepository.deleteById(id);
    }
}
