package joonggo.baechoo.controller;

import joonggo.baechoo.domain.Item;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.dto.ItemCreateDTO;
import joonggo.baechoo.dto.ItemUpdateDTO;
import joonggo.baechoo.repository.MemberRepository;
import joonggo.baechoo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final MemberRepository memberRepository;

    // 전체 조회
    @GetMapping("/list")
    public String showAllItems() {
        List<Item> itemList = itemService.getAllItems();

        return "items/list";
    }

    // 등록
    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("item", new ItemCreateDTO());

        return "items/form";
    }
    // 등록 처리
    @PostMapping("/new")
    public String createItem(@AuthenticationPrincipal User user,
                             @ModelAttribute ItemCreateDTO createDTO){
        Optional<Member> member = Optional.ofNullable(memberRepository.findByUserId(user.getUsername()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        itemService.createItem(member.orElseThrow().getId(), createDTO);

        return "redirect:/items/list";
    }

    //상세보기
    @GetMapping("/{id}")
    public String showItem(@PathVariable Long id, Model model){
        Item item = itemService.getItem(id);
        model.addAttribute("item", item);

        return "items/detail";
    }

    // 수정
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Item item = itemService.getItem(id);

        ItemUpdateDTO updateDTO = new ItemUpdateDTO();
        updateDTO.setName(item.getName());
        updateDTO.setPrice(item.getPrice());
        updateDTO.setDescription(item.getDescription());
        updateDTO.setCategory(item.getCategory());
        updateDTO.setStatus(item.getStatus());
        updateDTO.setImageUrl(item.getImageUrl());
        updateDTO.setCondition(item.getCondition());

        model.addAttribute("item", updateDTO);
        model.addAttribute("itemId", id);

        return "items/editForm";
    }

    // 수정 처리
    @PostMapping("/{id}/edit")
    public String updateItem(@PathVariable Long id, @ModelAttribute ItemUpdateDTO updateDTO){
        itemService.updateItem(id, updateDTO);
        return "redirect:/items/" + id;
    }

    // 삭제
    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id){
        itemService.deleteItem(id);

        return "redirect:/items/list";
    }

}
