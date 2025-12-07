package com.dat.backend_version_2.service.tournament.Poomsae;

import com.dat.backend_version_2.domain.achievement.PoomsaeList;
import com.dat.backend_version_2.domain.tournament.BracketNode;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeCombination;
import com.dat.backend_version_2.domain.tournament.Poomsae.PoomsaeHistory;
import com.dat.backend_version_2.dto.tournament.BracketNodeReq;
import com.dat.backend_version_2.dto.tournament.PoomsaeHistoryDTO;
import com.dat.backend_version_2.mapper.tournament.PoomsaeHistoryMapper;
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
import org.springframework.transaction.annotation.Isolation;
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
    private final PoomsaeListService poomsaeListService;
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

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createWinnerForRoundRobin(PoomsaeHistoryDTO poomsaeHistoryDTO) throws IdInvalidException {
        PoomsaeHistory poomsaeHistory = getPoomsaeHistoryById(poomsaeHistoryDTO.getIdPoomsaeHistory());
        if (poomsaeHistory == null) {
            throw new IdInvalidException("PoomsaeHistory not found");
        }

        PoomsaeList poomsaeList = poomsaeListService.getPoomsaeListById(poomsaeHistoryDTO.getReferenceInfo().getPoomsaeList());

        // Đánh dấu đã thắng
        poomsaeHistory.setHasWon(true);

        // Lấy target node cha
        int parentNode = poomsaeHistoryDTO.getNodeInfo().getTargetNode();
        int levelNextNode = poomsaeHistoryDTO.getNodeInfo().getLevelNode() - 1;

        if (levelNextNode < 0) {
            throw new IllegalStateException("Already at top level — cannot create higher node");
        }

        // Kiểm tra vị trí đó đã có người thắng chưa
        Optional<PoomsaeHistory> existingWinner = poomsaeHistoryRepository.findByIdPoomsaeCombinationAndLevelNodeAndSourceNode(
                UUID.fromString(poomsaeHistoryDTO.getReferenceInfo().getPoomsaeCombination()),
                levelNextNode,
                parentNode
        );
        existingWinner.ifPresent(poomsaeHistoryRepository::delete);

        // Tạo node mới cho vòng tiếp theo
        PoomsaeHistory winPoomsaeHistory = new PoomsaeHistory();
        winPoomsaeHistory.setSourceNode(parentNode);
        winPoomsaeHistory.setLevelNode(levelNextNode);
        winPoomsaeHistory.setPoomsaeList(poomsaeList);

        poomsaeHistoryRepository.save(winPoomsaeHistory);
    }

    @Transactional
    public String createWinnerForElimination(PoomsaeHistoryDTO poomsaeHistoryDTO, int participants) throws IdInvalidException {
        // Lấy lịch sử thi đấu hiện tại
        PoomsaeHistory poomsaeHistory = getPoomsaeHistoryById(poomsaeHistoryDTO.getIdPoomsaeHistory());
        if (poomsaeHistory == null) {
            throw new IdInvalidException("PoomsaeHistory not found");
        }

        // Đánh dấu đã thắng
        poomsaeHistory.setHasWon(true);
        poomsaeHistoryRepository.save(poomsaeHistory);

        // Lấy target node cha
        Integer parentNode = Optional.ofNullable(
                bracketNodeService.getBracketNodeByParticipantsAndChildNodeId(participants, poomsaeHistory.getTargetNode())
        ).map(BracketNode::getParentNodeId).orElse(null);

        // Đánh dấu thua cho đối thủ cùng parentNode(nếu có)
        List<PoomsaeHistory> siblings = poomsaeHistoryRepository.findAllByTargetNodeAndIdPoomsaeCombination(
                poomsaeHistory.getTargetNode(),
                poomsaeHistory.getPoomsaeList().getPoomsaeCombination().getIdPoomsaeCombination()
        );

        for (PoomsaeHistory sibling : siblings) {
            if (!sibling.getIdPoomsaeHistory().equals(poomsaeHistory.getIdPoomsaeHistory())) {
                sibling.setHasWon(false);
                poomsaeHistoryRepository.save(sibling);

                if (poomsaeHistory.getLevelNode() == 2) {
                    PoomsaeHistory losePoomsaeHistory = new PoomsaeHistory();
                    losePoomsaeHistory.setLevelNode(-1);
                    losePoomsaeHistory.setSourceNode(sibling.getTargetNode());
                    losePoomsaeHistory.setTargetNode(-1);
                    losePoomsaeHistory.setPoomsaeList(sibling.getPoomsaeList());
                    poomsaeHistoryRepository.save(losePoomsaeHistory);
                }
            }
        }

        if ((poomsaeHistoryDTO.getNodeInfo().getTargetNode() != 0 && poomsaeHistoryDTO.getNodeInfo().getTargetNode() != -1)
                && parentNode == null) {
            throw new IdInvalidException("Parent node not found for targetNode " + poomsaeHistory.getTargetNode());
        }

        // Tạo node mới cho vòng tiếp theo
        PoomsaeHistory winPoomsaeHistory = new PoomsaeHistory();
        winPoomsaeHistory.setLevelNode(poomsaeHistory.getLevelNode() - 1);
        winPoomsaeHistory.setSourceNode(poomsaeHistory.getTargetNode());
        winPoomsaeHistory.setTargetNode(parentNode);

        // Sao chép thông tin liên kết
        winPoomsaeHistory.setPoomsaeList(poomsaeHistory.getPoomsaeList());

        poomsaeHistoryRepository.save(winPoomsaeHistory);

        return winPoomsaeHistory.getIdPoomsaeHistory().toString();
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

    public boolean checkPoomsaeHistoryExists(String idTournament, String idCombination, String idAccount) {
        return poomsaeHistoryRepository.existsByFilter(
                idTournament != null ? UUID.fromString(idTournament) : null,
                idCombination != null ? UUID.fromString(idCombination) : null,
                idAccount != null ? studentService.getStudentByIdAccount(idAccount).getIdUser() : null
        );
    }
}
