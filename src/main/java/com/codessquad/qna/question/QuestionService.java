package com.codessquad.qna.question;

import com.codessquad.qna.user.User;
import com.codessquad.qna.user.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public List<Question> getQuestions() {
        return StreamSupport.stream(questionRepository.findAll().spliterator(), true)
                .collect(Collectors.toList());
    }

    public Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다. id : " + id));
    }

    public Question getQuestion(Long id, UserDTO currentSessionUser) {
        Question question = getQuestion(id);

        verifyWriter(question, currentSessionUser.toEntity());

        return question;
    }

    public void createQuestion(Question question, UserDTO currentSessionUser) {
        verifyWriter(question, currentSessionUser.toEntity());

        questionRepository.save(question);
    }

    public void updateQuestion(Long id, Question newQuestion, UserDTO currentSessionUser) {
        Question existedQuestion = getQuestion(id);

        verifyWriter(existedQuestion, currentSessionUser.toEntity());

        existedQuestion.update(newQuestion);
        questionRepository.save(existedQuestion);
    }

    @Transactional
    public void deleteQuestion(Long id, UserDTO currentSessionUser) {
        Question question = getQuestion(id);

        verifyWriter(question, currentSessionUser.toEntity());

        deleteAnswers(question.getAnswers());
        questionRepository.delete(question);
    }

    private void verifyWriter(Question existedQuestion, User target) {
        if (!existedQuestion.getWriter().isIdSameAs(target.getId())) {
            throw HttpClientErrorException.create(
                    HttpStatus.FORBIDDEN,
                    "",
                    null,
                    null,
                    StandardCharsets.UTF_8
            );
        }
    }

    private Answer getAnswer(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 답변입니다."));
    }

    public void createAnswer(Answer answer) {
        answerRepository.save(answer);
    }

    public void deleteAnswers(List<Answer> answers) {
        answerRepository.deleteAll(answers);
    }

    public void deleteAnswer(Long id, UserDTO currentSessionUser) {
        Answer answer = getAnswer(id);

        if (!answer.getWriter().isIdSameAs(currentSessionUser.getId())) {
            throw HttpClientErrorException.create(
                    HttpStatus.FORBIDDEN,
                    "",
                    null,
                    null,
                    StandardCharsets.UTF_8
            );
        }

        answerRepository.delete(answer);
    }
}
