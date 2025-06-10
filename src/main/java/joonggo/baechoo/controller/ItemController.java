package joonggo.baechoo.controller;

import joonggo.baechoo.domain.Item;
import joonggo.baechoo.domain.Member;
import joonggo.baechoo.dto.ItemCreateDTO;
import joonggo.baechoo.dto.ItemUpdateDTO;
import joonggo.baechoo.repository.MemberRepository;
import joonggo.baechoo.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final MemberRepository memberRepository;

    @Value("${file.dir}")
    private String fileDir;

    // 전체 조회
    @GetMapping("/list")
    public String showAllItems(Model model) {
        List<Item> itemList = itemService.getAllItems();
        model.addAttribute("itemList", itemList);
        return "items/list";
    }

    // 등록
    @GetMapping("/new")
    public String showCreateForm(Model model){
        model.addAttribute("item", new ItemCreateDTO());

        return "items/registerForm";
    }
    // 등록 처리
    @PostMapping("/new")
    public String createItem(@AuthenticationPrincipal User user,
                             @ModelAttribute ItemCreateDTO createDTO) throws IOException {
        // 파일 저장
        MultipartFile imageFile = createDTO.getImageFile();
        if(imageFile != null && !imageFile.isEmpty()){
            String originalFileName = imageFile.getOriginalFilename();
            String storedFileName = createStoredFileName(originalFileName);

            imageFile.transferTo(new File(fileDir + storedFileName));
            createDTO.setImageUrl(storedFileName);
        }

        Optional<Member> member = Optional.ofNullable(memberRepository.findByUserId(user.getUsername()).orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다.")));

        itemService.createItem(member.orElseThrow().getId(), createDTO);

        return "redirect:/items/list";
    }

    //상세보기
    @GetMapping("/{id}")
    public String showItem(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal User user){
        Item item = itemService.getItem(id);
        model.addAttribute("item", item);

        if(user != null) {
            memberRepository.findByUserId(user.getUsername()).ifPresent(currentMember -> model.addAttribute("currentMember", currentMember) );
        }

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
    public String updateItem(@PathVariable Long id, @ModelAttribute ItemUpdateDTO updateDTO) throws IOException {

        MultipartFile imageFile = updateDTO.getImageFile();
        if (imageFile != null && !imageFile.isEmpty()) {

            String originalFilename = imageFile.getOriginalFilename();
            String storedFileName = createStoredFileName(originalFilename);
            imageFile.transferTo(new File(fileDir + storedFileName));
            updateDTO.setImageUrl(storedFileName);
        }

        itemService.updateItem(id, updateDTO);
        return "redirect:/items/" + id;
    }

    // 삭제
    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Long id){
        itemService.deleteItem(id);

        return "redirect:/items/list";
    }

    private String createStoredFileName(String originalFileName){
        String ext = extractExt(originalFileName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }
    private String extractExt(String originalFileName){
        int pos = originalFileName.lastIndexOf(".");
        if(pos != -1 && originalFileName.length() > pos + 1){
            return originalFileName.substring(pos + 1);
        }
        return "";
    }

}
