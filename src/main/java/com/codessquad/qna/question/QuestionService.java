package com.codessquad.qna.question;

import com.codessquad.qna.answer.AnswerService;
import com.codessquad.qna.exception.ResourceNotFoundException;
import com.codessquad.qna.user.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerService answerService;

    public QuestionService(QuestionRepository questionRepository, AnswerService answerService) {
        this.questionRepository = questionRepository;
        this.answerService = answerService;
    }

    public Page<QuestionDTO> readAll(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, 15, Sort.by("createDateTime", "id"));
        Page<Question> questions = questionRepository.findAllByDeletedFalse(pageRequest);

        return questions.map(question -> QuestionDTO.of(question, answerService.countBy(question)));
    }

    private Question readExistedQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("질문이 존재하지 않습니다. id : " + id));
    }

    public QuestionDTO read(Long id) {
        return QuestionDTO.of(readExistedQuestion(id), answerService.readAll(id));
    }

    public QuestionDTO readVerifiedQuestion(Long id, UserDTO user) {
        Question result = readExistedQuestion(id);

        result.verifyWriter(user.toEntity());

        return QuestionDTO.from(result);
    }

    public void create(QuestionDTO question) {
        questionRepository.save(question.toEntity());
    }

    public void update(Long id, QuestionDTO newQuestion) {
        Question existedQuestion = readExistedQuestion(id);

        existedQuestion.update(newQuestion.toEntity());
        questionRepository.save(existedQuestion);
    }

    @Transactional
    public void delete(Long id, UserDTO currentSessionUser) {
        Question question = readExistedQuestion(id);
        question.checkDeletable(currentSessionUser.toEntity());

        answerService.deleteAll(question.getAnswers());
        question.delete();
        questionRepository.save(question);
    }
}
