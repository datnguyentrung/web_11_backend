package com.dat.backend_version_2.service.tournament.Poomsae;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.domain.tournament.BracketNode;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeHistory;
import com.dat.backend_version_2.dto.tournament.BracketNodeReq;
import com.dat.backend_version_2.dto.tournament.PoomsaeCombinationDTO;
import com.dat.backend_version_2.dto.tournament.PoomsaeHistoryDTO;
import com.dat.backend_version_2.dto.tournament.TournamentDTO;
import com.dat.backend_version_2.enums.tournament.PoomsaeMode;
import com.dat.backend_version_2.mapper.tournament.PoomsaeHistoryMapper;
import com.dat.backend_version_2.mapper.tournament.TournamentMapper;
import com.dat.backend_version_2.repository.achievement.PoomsaeListRepository;
import com.dat.backend_version_2.repository.tournament.Poomsae.PoomsaeHistoryRepository;
import com.dat.backend_version_2.service.achievement.PoomsaeListService;
import com.dat.backend_version_2.service.tournament.BracketNodeService;
import com.dat.backend_version_2.service.training.StudentService;
import com.dat.backend_version_2.util.error.IdInvalidException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PoomsaeHistoryService {
    private final PoomsaeHistoryRepository poomsaeHistoryRepository;
    private final BracketNodeService bracketNodeService;
    private final PoomsaeListRepository poomsaeListRepository;
    private final PoomsaeCombinationService poomsaeCombinationService;
    private static final Logger log = LoggerFactory.getLogger(PoomsaeHistoryService.class);
    private final StudentService studentService;

    public PoomsaeHistory getPoomsaeHistoryById(String id) throws IdInvalidException {
        return poomsaeHistoryRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new IdInvalidException("Poomsae history not found with id: " + id));
    }

    @Transactional
    public void createPoomsaeHistoryForRoundRobin(List<String> idPoomsaeList) throws IdInvalidException {
        List<UUID> uuidList = idPoomsaeList.stream()
                .map(UUID::fromString)
                .toList();

        List<PoomsaeList> poomsaeLists = poomsaeListRepository.findAllByIdPoomsaeList(uuidList);

        if (poomsaeLists.isEmpty()) {
            throw new IdInvalidException("No valid PoomsaeList found for provided IDs");
        }

        int index = 0;
        List<PoomsaeHistory> poomsaeHistories = new ArrayList<>();
        if (poomsaeLists.size() > 8) {
            for (PoomsaeList poomsaeList : poomsaeLists) {
                PoomsaeHistory poomsaeHistory = new PoomsaeHistory();
                poomsaeHistory.setPoomsaeList(poomsaeList);
                poomsaeHistory.setLevelNode(2);
                poomsaeHistory.setSourceNode(index++);
                poomsaeHistories.add(poomsaeHistory);
            }
        } else {
            for (PoomsaeList poomsaeList : poomsaeLists) {
                PoomsaeHistory poomsaeHistory = new PoomsaeHistory();
                poomsaeHistory.setPoomsaeList(poomsaeList);
                poomsaeHistory.setLevelNode(1);
                poomsaeHistory.setSourceNode(index++);
                poomsaeHistories.add(poomsaeHistory);
            }
        }
        poomsaeHistoryRepository.saveAll(poomsaeHistories);
    }

    @Transactional
    public void createPoomsaeHistoryForNode(List<String> idPoomsaeList) throws IdInvalidException {
        // 1️⃣ Lấy toàn bộ danh sách trong 1 lần query
        List<UUID> uuidList = idPoomsaeList.stream()
                .map(UUID::fromString)
                .toList();

        List<PoomsaeList> poomsaeLists = poomsaeListRepository.findAllByIdPoomsaeList(uuidList);

        if (poomsaeLists.isEmpty()) {
            throw new IdInvalidException("No valid PoomsaeList found for provided IDs");
        }

        int participants = poomsaeLists.size();

        // 2️⃣ Lấy danh sách sơ đồ thi đấu
        List<BracketNodeReq.BracketInfo> bracketInfos = bracketNodeService
                .getBracketNodeByParticipants(participants);

        // 3️⃣ Chuẩn bị iterator để gán tuần tự (không quan tâm thứ tự)
        Iterator<PoomsaeList> iterator = poomsaeLists.iterator();

        // 4️⃣ Duyệt qua từng node và tạo PoomsaeHistory
        for (BracketNodeReq.BracketInfo bracketInfo : bracketInfos) {
            if (bracketInfo.getBracketNodes().size() == 1 && iterator.hasNext()) {
                PoomsaeHistory poomsaeHistory = new PoomsaeHistory();
                poomsaeHistory.setPoomsaeList(iterator.next());
//                poomsaeHistory.setPoomsaeCombination();
                poomsaeHistory.setSourceNode(bracketInfo.getChildNodeId());
                poomsaeHistory.setTargetNode(
                        bracketNodeService.getBracketNodeByParticipantsAndChildNodeId(
                                participants,
                                bracketInfo.getChildNodeId()
                        ).getParentNodeId()
                );
                poomsaeHistory.setLevelNode(bracketInfo.getLevelNode());

                poomsaeHistoryRepository.save(poomsaeHistory);
            }
        }
    }

    public List<PoomsaeHistory> getAllPoomsaeHistory() {
        return poomsaeHistoryRepository.findAll();
    }

    public List<PoomsaeHistoryDTO> getAllPoomsaeHistoryByIdPoomsaeCombination(
            String idPoomsaeCombination) throws IdInvalidException {
        PoomsaeCombination combination = poomsaeCombinationService.getCombinationById(idPoomsaeCombination);
        return poomsaeHistoryRepository.findAllByPoomsaeCombination(combination).stream()
                .map(PoomsaeHistoryMapper::poomsaeHistoryToPoomsaeHistoryDTO)
                .toList();
    }

    @Transactional
    public void deleteAllPoomsaeHistoryByIdPoomsaeCombination(String idPoomsaeCombination) throws IdInvalidException {
        PoomsaeCombination combination = poomsaeCombinationService.getCombinationById(idPoomsaeCombination);
        List<PoomsaeHistory> histories = poomsaeHistoryRepository.findAllByPoomsaeCombination(combination);
        poomsaeHistoryRepository.deleteAll(histories);
    }

//    @Transactional(isolation = Isolation.SERIALIZABLE)
//    public void createWinnerForRoundRobin(PoomsaeHistoryDTO poomsaeHistoryDTO) throws IdInvalidException {
//        PoomsaeHistory poomsaeHistory = getPoomsaeHistoryById(poomsaeHistoryDTO.getIdPoomsaeHistory());
//        if (poomsaeHistory == null) {
//            throw new IdInvalidException("PoomsaeHistory not found");
//        }
//
//        PoomsaeList poomsaeList = poomsaeListService.getPoomsaeListById(poomsaeHistoryDTO.getReferenceInfo().getPoomsaeList());
//
//        // Đánh dấu đã thắng
//        poomsaeHistory.setHasWon(true);
//
//        // Lấy target node cha
//        int parentNode = poomsaeHistoryDTO.getNodeInfo().getTargetNode();
//        int levelNextNode = poomsaeHistoryDTO.getNodeInfo().getLevelNode() - 1;
//
//        if (levelNextNode < 0) {
//            throw new IllegalStateException("Already at top level — cannot create higher node");
//        }
//
//        // Kiểm tra vị trí đó đã có người thắng chưa
//        Optional<PoomsaeHistory> existingWinner = poomsaeHistoryRepository.findByIdPoomsaeCombinationAndLevelNodeAndSourceNode(
//                UUID.fromString(poomsaeHistoryDTO.getReferenceInfo().getPoomsaeCombination()),
//                levelNextNode,
//                parentNode
//        );
//        existingWinner.ifPresent(poomsaeHistoryRepository::delete);
//
//        // Tạo node mới cho vòng tiếp theo
//        PoomsaeHistory winPoomsaeHistory = new PoomsaeHistory();
//        winPoomsaeHistory.setSourceNode(parentNode);
//        winPoomsaeHistory.setLevelNode(levelNextNode);
//        winPoomsaeHistory.setPoomsaeList(poomsaeList);
//
//        poomsaeHistoryRepository.save(winPoomsaeHistory);
//    }

    @Transactional
    public void createWinnerForElimination(String idHistory) throws IdInvalidException {
        // 1. Lấy lịch sử người thắng
        PoomsaeHistory winner = poomsaeHistoryRepository.findByIdWithCombination(UUID.fromString(idHistory))
                .orElseThrow(() -> new IdInvalidException("PoomsaeHistory not found"));

        // 2. Cập nhật người thắng
        winner.setHasWon(true);
        poomsaeHistoryRepository.save(winner); // Hibernate có thể tự batch update cuối transaction, nhưng save explicit cho rõ ràng

        /// 3. Tìm đối thủ (Sibling) - Query tối ưu
        Optional<PoomsaeHistory> opponentOpt = poomsaeHistoryRepository.findOpponentByNode(
                winner.getPoomsaeList().getTournament().getIdTournament(),
                winner.getPoomsaeList().getPoomsaeCombination().getIdPoomsaeCombination(),
                winner.getTargetNode(),
                winner.getIdPoomsaeHistory()
        );

        // 4. Xử lý người thua (nếu có đối thủ)
        if (opponentOpt.isPresent()) {
            PoomsaeHistory opponent = opponentOpt.get();
            opponent.setHasWon(false);
            poomsaeHistoryRepository.save(opponent);

            // Logic tạo nhánh thua (Tranh giải 3 hoặc tương tự)
            if (winner.getLevelNode() == 2) {
                PoomsaeHistory loseHistory = new PoomsaeHistory();
                loseHistory.setLevelNode(-1);
                loseHistory.setSourceNode(opponent.getTargetNode());
                loseHistory.setTargetNode(-1);
                loseHistory.setPoomsaeList(opponent.getPoomsaeList()); // Hibernate chỉ copy reference ID, không cần fetch Student
                poomsaeHistoryRepository.save(loseHistory);
            }
        } else {
            // Tùy nghiệp vụ: Có thể throw lỗi nếu bắt buộc phải có đối thủ, hoặc bỏ qua nếu là vòng bye (miễn đấu)
            // throw new IdInvalidException("Sibling not found...");
        }

//        int participants = poomsaeListRepository.countDistinctParticipantsInCombination(
//                winner.getPoomsaeList().getTournament().getIdTournament(),
//                winner.getPoomsaeList().getPoomsaeCombination().getIdPoomsaeCombination()
//        );
        System.out.println("hello");
        int participants = winner.getPoomsaeList().getPoomsaeCombination().getParticipants();
        System.out.println("participants: " + participants);
        // 5. Tính toán Parent Node
        Integer parentNodeId = Optional.ofNullable(
                bracketNodeService.getBracketNodeByParticipantsAndChildNodeId(participants, winner.getTargetNode())
        ).map(BracketNode::getParentNodeId).orElse(null);

        // Validate Parent Node
        if ((winner.getTargetNode() != 0 && winner.getTargetNode() != -1) && parentNodeId == null) {
            throw new IdInvalidException("Parent node not found for targetNode " + winner.getTargetNode());
        }

        // 6. Tạo Node thắng tiến vào vòng trong
        PoomsaeHistory nextRoundHistory = new PoomsaeHistory();
        nextRoundHistory.setLevelNode(winner.getLevelNode() - 1);
        nextRoundHistory.setSourceNode(winner.getTargetNode());
        nextRoundHistory.setTargetNode(parentNodeId);
        nextRoundHistory.setPoomsaeList(winner.getPoomsaeList());

        poomsaeHistoryRepository.save(nextRoundHistory);
    }

    /**
     * Xóa một bản ghi PoomsaeHistory và cập nhật trạng thái các node con liên quan.
     * <p>
     * Quy trình:
     * 1. Lấy thông tin PoomsaeHistory cần xóa.
     * 2. Tìm tất cả node con (child nodes) trong cây bracket tương ứng.
     * 3. Lấy toàn bộ PoomsaeHistory trong cùng tổ hợp (combination).
     * 4. Tạo Map để truy cập nhanh theo childNodeId.
     * 5. Cập nhật trạng thái hasWon=false cho các node con.
     * 6. Cuối cùng, xóa bản ghi hiện tại.
     *
     * @param participants     Số lượng người tham gia (để xác định bracket).
     * @param idPoomsaeHistory ID của bản ghi cần xóa.
     * @throws IdInvalidException Nếu không tìm thấy bản ghi hoặc dữ liệu không hợp lệ.
     */
    public void deletePoomsaeHistoryForElimination(int participants, String idPoomsaeHistory) throws IdInvalidException {

        // 1️⃣ Lấy ra bản ghi PoomsaeHistory cần xóa
        PoomsaeHistory poomsaeHistory = getPoomsaeHistoryById(idPoomsaeHistory);

        // 2️⃣ Lấy danh sách các child node ID (các node con của source node hiện tại)
        List<Integer> childNodeIds = bracketNodeService
                .getBracketNodeByParticipantsAndParentNodeId(participants, poomsaeHistory.getSourceNode())
                .stream()
                .map(BracketNode::getChildNodeId)
                .toList();

        // 3️⃣ Lấy toàn bộ danh sách PoomsaeHistory thuộc cùng tổ hợp (combination)
        List<PoomsaeHistory> combination = poomsaeHistoryRepository.findAllByPoomsaeCombination(
                poomsaeHistory.getPoomsaeList().getPoomsaeCombination());

        // 4️⃣ Tạo map để tra cứu nhanh PoomsaeHistory theo targetNode (childNodeId)
        Map<Integer, PoomsaeHistory> poomsaeHistoryMap = combination.stream()
                .collect(Collectors.toMap(
                        PoomsaeHistory::getSourceNode,       // key: childNodeId (sourceNode)
                        Function.identity(),                  // value: đối tượng PoomsaeHistory
                        (existing, replacement) -> existing    // giữ bản ghi đầu tiên nếu trùng key
                ));

        // 5️⃣ Duyệt qua từng child node, cập nhật trạng thái hasWon=false
        for (Integer childNodeId : childNodeIds) {
            PoomsaeHistory childHistory = poomsaeHistoryMap.get(childNodeId);

            if (childHistory == null) {
                // Nếu không tìm thấy, ném lỗi rõ ràng
//                throw new IdInvalidException("Không tìm thấy PoomsaeHistory cho childNodeId: " + childNodeId);
                log.warn("Không tìm thấy PoomsaeHistory cho childNodeId: {}", childNodeId);
                continue;
            }

            // Cập nhật trạng thái thắng thành false
            childHistory.setHasWon(false);
            poomsaeHistoryRepository.save(childHistory);
        }

        // 6️⃣ Xóa node cha nếu node hiện tại chiến thắng
        PoomsaeHistory parentPoomsaeHistory = poomsaeHistoryMap.get(poomsaeHistory.getTargetNode());
        if (parentPoomsaeHistory != null) {
            poomsaeHistoryRepository.delete(parentPoomsaeHistory);
        }

        // 7️⃣ Xóa bản ghi gốc
        poomsaeHistoryRepository.delete(poomsaeHistory);
    }

    public void deletePoomsaeHistoryForRoundRobin(String idPoomsaeHistory) throws IdInvalidException {
        PoomsaeHistory poomsaeHistory = getPoomsaeHistoryById(idPoomsaeHistory);
        if (poomsaeHistory == null) {
            throw new IdInvalidException("PoomsaeHistory not found with id: " + idPoomsaeHistory);
        }
        poomsaeHistoryRepository.delete(poomsaeHistory);
    }

    public Integer countPoomsaeHistoryByLevelNode(int levelNode) {
        return poomsaeHistoryRepository.countPoomsaeHistoryByLevelNode(levelNode);
    }

    public List<PoomsaeHistoryDTO> getAllPoomsaeHistoryByIdTournament(String idTournament) throws IdInvalidException {
        return poomsaeHistoryRepository.findAllByIdTournament(
                        UUID.fromString(idTournament))
                .stream()
                .map(PoomsaeHistoryMapper::poomsaeHistoryToPoomsaeHistoryDTO)
                .toList();
    }

    public PoomsaeCombinationDTO.CheckModeResponse checkPoomsaeMode(String idTournament, String idCombination, String idAccount) {
        Optional<String> result = poomsaeHistoryRepository.findModeByFilter(
                idTournament != null ? UUID.fromString(idTournament) : null,
                idCombination != null ? UUID.fromString(idCombination) : null,
                idAccount != null ? studentService.getStudentByIdAccount(idAccount).getIdUser() : null
        );
        // Xử lý kết quả (Mapping)
        if (result.isPresent()) {
            // TRƯỜNG HỢP CÓ DỮ LIỆU (TRUE)
            // Convert String từ DB sang Enum
            PoomsaeMode mode = PoomsaeMode.valueOf(result.get());
            return new PoomsaeCombinationDTO.CheckModeResponse(true, mode);
        } else {
            // TRƯỜNG HỢP KHÔNG CÓ DỮ LIỆU (FALSE)
            return new PoomsaeCombinationDTO.CheckModeResponse(false, null);
        }
    }

    public List<TournamentDTO.HistoryInfo> getPoomsaeHistoryByFilter(String idTournament, String idCombination, String idAccount) {
        return poomsaeHistoryRepository.findByFilter(
                        UUID.fromString(idTournament),
                        UUID.fromString(idCombination),
                        idAccount != null ? studentService.getStudentByIdAccount(idAccount).getIdUser() : null
                )
                .stream()
                .map(TournamentMapper::historyInfoQuickViewToHistoryInfo)
                .toList();
    }
}
