package joonggo.baechoo.service;

import jakarta.persistence.EntityNotFoundException;
import joonggo.baechoo.domain.Condition;
import joonggo.baechoo.domain.Item;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.domain.Status;
import joonggo.baechoo.dto.ItemCreateDTO;
import joonggo.baechoo.dto.ItemUpdateDTO;
import joonggo.baechoo.repository.ItemRepository;
import joonggo.baechoo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public Item createItem(Long memberId, ItemCreateDTO createDTO) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 업습니다."));

        Item item = new Item();
        item.setName(createDTO.getName());
        item.setPrice(createDTO.getPrice());
        item.setDescription(createDTO.getDescription());
        item.setCategory(createDTO.getCategory());
        item.setStatus(Status.valueOf(String.valueOf(createDTO.getStatus())));
        item.setCondition(Condition.valueOf(String.valueOf(createDTO.getCondition())));
        item.setImageUrl(createDTO.getImageUrl());
        item.setMember(member);


        return itemRepository.save(item);
    }

    public Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(()-> new EntityNotFoundException("상품을 찾을 수 없습니다."));
    }

    public List<Item> getAllItems() {
        return itemRepository.findAllWithMember();
    }
    public List<Item> getAllItemsByMemberId(Long memberId){
        return itemRepository.findByMemberId(memberId);
    }

    public Item updateItem(Long itemId, ItemUpdateDTO updateDTO) {
        Item item = getItem(itemId);
        item.setName(updateDTO.getName());
        item.setPrice(updateDTO.getPrice());
        item.setDescription(updateDTO.getDescription());
        item.setCategory(updateDTO.getCategory());
        item.setStatus(Status.valueOf(String.valueOf(updateDTO.getStatus())));
        item.setCondition(Condition.valueOf(String.valueOf(updateDTO.getCondition())));
        item.setImageUrl(updateDTO.getImageUrl());

        return itemRepository.save(item);
    }

    public void deleteItem(Long itemId){
        itemRepository.deleteById(itemId);
    }

    public List<Item> findTop5RecentItems(){
        return itemRepository.findTop5ByOrderByCreatedDateDesc();
    }


}
