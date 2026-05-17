package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.impl;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Question;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.QuestionRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;

    @Override
    public Question addQuestion(Question question) {

        if (question.getOptions() != null) {
            question.getOptions().forEach(option -> option.setQuestion(question));
        }
        return questionRepository.save(question);
    }


    @Override
    public Question updateQuestion(Integer id, Question questionDetails) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));

        existingQuestion.setQuestionText(questionDetails.getQuestionText());
        existingQuestion.setQuestionType(questionDetails.getQuestionType());
        existingQuestion.setMarks(questionDetails.getMarks());

        return questionRepository.save(existingQuestion);
    }

    @Override
    public List<Question> getQuestionBank() {
        return questionRepository.findAll();
    }

    @Override
    public Question getQuestionById(Integer id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    @Override
    public void deleteQuestion(Integer id) {
        questionRepository.deleteById(id);
    }
}