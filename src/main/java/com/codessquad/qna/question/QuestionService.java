package com.codessquad.qna.question;

import com.codessquad.qna.exception.ResourceNotFoundException;
import com.codessquad.qna.user.UserDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    public QuestionService(QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    public List<Question> readAll() {
        return questionRepository.findAll();
    }

    public Question read(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("질문이 존재하지 않습니다. id : " + id));
    }

    public Question readVerifiedQuestion(Long id, UserDTO user) {
        Question result = read(id);

        result.verifyWriter(user.toEntity());

        return result;
    }

    public void create(Question question) {
        questionRepository.save(question);
    }

    public void update(Long id, Question newQuestion) {
        Question existedQuestion = read(id);

        existedQuestion.update(newQuestion);
        questionRepository.save(existedQuestion);
    }

    @Transactional
    public void delete(Long id, UserDTO currentSessionUser) {
        Question question = readVerifiedQuestion(id, currentSessionUser);

        deleteAnswers(question.getAnswers());
        questionRepository.delete(question);
    }

    private Answer readAnswer(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 답변입니다. id : " + id));
    }

    public Answer readVerifiedAnswer(Long id, UserDTO user) {
        Answer answer = readAnswer(id);

        answer.verifyWriter(user.toEntity());

        return answer;
    }

    public void createAnswer(Answer answer) {
        answerRepository.save(answer);
    }

    public void updateAnswer(Long id, Answer newAnswer) {
        Answer existedAnswer = readAnswer(id);

        existedAnswer.update(newAnswer);
        answerRepository.save(existedAnswer);
    }

    public void deleteAnswers(List<Answer> answers) {
        answerRepository.deleteAll(answers);
    }

    public void deleteAnswer(Long id, UserDTO currentSessionUser) {
        Answer answer = readVerifiedAnswer(id, currentSessionUser);

        answerRepository.delete(answer);
    }
}
