package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.model.Question;
import java.util.List;

public interface QuestionService {
    Question addQuestion(Question question);
    List<Question> getQuestionBank();
    Question getQuestionById(Integer id);
    void deleteQuestion(Integer id);
    Question updateQuestion(Integer id, Question questionDetails);
}
