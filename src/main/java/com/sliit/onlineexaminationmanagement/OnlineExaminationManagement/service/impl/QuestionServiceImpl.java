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
        // ප්‍රශ්නය සහ පිළිතුරු (Options) එකිනෙකට සම්බන්ධ කිරීම
        if (question.getOptions() != null) {
            question.getOptions().forEach(option -> option.setQuestion(question));
        }
        return questionRepository.save(question);
    }

    @Override
    public List<Question> getQuestionBank() {
        return questionRepository.findAll();
    }

    @Override
    public void deleteQuestion(Integer id) {
        questionRepository.deleteById(id);
    }
}