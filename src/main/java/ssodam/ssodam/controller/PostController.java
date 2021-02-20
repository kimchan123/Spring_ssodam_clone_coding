package ssodam.ssodam.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ssodam.ssodam.domain.*;
import ssodam.ssodam.service.CategoryService;
import ssodam.ssodam.service.CommentService;
import ssodam.ssodam.service.MemberService;
import ssodam.ssodam.service.PostService;

@Controller
@RequiredArgsConstructor
// http://www.ssodam.com/content/1099804?prev=3&prev_content=/board/3
//                                postId?이전페이지&카테고리
public class PostController {
    private final MemberService memberService;
    private final PostService postService;
    private final CategoryService categoryService;
    private final CommentService commentService;


    @GetMapping("content/{postId}")
    public String board(@PathVariable("postId") Long postId,
                        @RequestParam("prev") Long prev,
                        @RequestParam("prev_content") String prev_content,
                        Model model){

        Post post = postService.findOne(postId);
        postService.increaseVisit(post);
        System.out.println("prev = " + prev);
        System.out.println("prev_content = " + prev_content);   //분리


        model.addAttribute("post", post);
        return "content";       //이따 만들자
    }

    @GetMapping("/board/{categoryId}")
    public String postView(@PathVariable("categoryId") Long categoryId, @PageableDefault Pageable pageable, Model model) {
        Category category = categoryService.findOne(categoryId);
        Page<Post> boardList = postService.getPostListByCategory(category, pageable);
        model.addAttribute("boardList", boardList);
        model.addAttribute("category", category);
        return "board";
    }

    @GetMapping("/write/{categoryId}")
    public String post(@PathVariable("categoryId") Long categoryId, Model model) {
        Category category = categoryService.findOne(categoryId);
        model.addAttribute("categoryName", categoryId);
        return "write";
    }

    @PostMapping("/write/{categoryId}")
    public String writePost(@PathVariable("categoryId") Long categoryId, @AuthenticationPrincipal Member currentMember, PostForm postForm){
        Category category = categoryService.findOne(categoryId);
        postForm.setCategory(category);
        postForm.setMember(currentMember);
        postService.post(postForm);

        return "redirect:/board/{categoryId}";
    }

    @PostMapping("content/{postId}/comments")
    public String writeComment(@PathVariable("postId") Long postId, CommentForm form, @AuthenticationPrincipal Member currentMember) {
        commentService.writeComment(currentMember.getId(), postId, form.getContents());
        return "redirect:/content/{postId}";
    }

    @PostMapping("content/{postId}/{commentId}")
    public String writeSubComment(@PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId,
                                  CommentForm form,@AuthenticationPrincipal Member currentMember){
        Post post = postService.findOne(postId);
        commentService.writeSubcomment(currentMember.getId(), commentId, form.getContents());
        return "redirect:/content/{postId}";
    }

    @GetMapping("content/{postId}/{comment_id}/update")
    public String updateCommentView(@PathVariable("postId") Long postId, @PathVariable("comment_id") Long commentId, Model model) {
        CommentForm form = new CommentForm();
        Comment comment = commentService.findOne(commentId);
        form.setContents(comment.getContent());
        // 모델 추가? 해당 포스트의 댓글 목록 다시?
        model.addAttribute("commentForm", form);
        return "redirect:/content/{postId}";
    }

    @PatchMapping("content/{postId}/{comment_id}/update")
    public String updateComment(@PathVariable("postId") Long postId, @PathVariable("comment_id") Long commentId,
                                CommentForm form, @AuthenticationPrincipal Member currentMember) {
        commentService.updateComment(currentMember.getId(), commentId, form.getContents());
        return "redirect:/content/{postId}";
    }

    @DeleteMapping("/{postId}/{comment_id}/delete")
    public String deleteComment(@PathVariable("postId") Long postId, @PathVariable("comment_id") Long commentId,
                                @AuthenticationPrincipal Member currentMember) {
        commentService.deleteComment(currentMember.getId(), postId, commentId);
        return "redirect:/content/{postId}";
    }
}
