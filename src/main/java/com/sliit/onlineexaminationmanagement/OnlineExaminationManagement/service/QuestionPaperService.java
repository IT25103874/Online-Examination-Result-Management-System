package com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.service;

import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.Question;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.entity.QuestionPaper;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.QuestionPaperRepository;
import com.sliit.onlineexaminationmanagement.OnlineExaminationManagement.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionPaperService {

    private final QuestionRepository questionRepository;
    private final QuestionPaperRepository paperRepository;

    public QuestionPaper generateAutoPaper(Integer subjectId, Integer count, String difficulty, String paperName, String instructions) {

        List<Question> allQuestions = questionRepository.findBySubjectSubjectIdAndDifficultyLevel(subjectId, difficulty);




        if (allQuestions == null || allQuestions.isEmpty()) {
            throw new RuntimeException("Unable to create the question paper! There are no questions in the database for this subject and difficulty level.");
        }


        if (allQuestions.size() < count) {
            throw new RuntimeException("Not enough questions! You requested " + count + " questions, but there are only " + allQuestions.size() + " questions in the system.");
        }


        Collections.shuffle(allQuestions);

        List<Question> selected = allQuestions.stream().limit(count).collect(Collectors.toList());


        QuestionPaper paper = new QuestionPaper();
        paper.setPaperName(paperName);
        paper.setInstructions(instructions);
        paper.setQuestions(selected);

        return paperRepository.save(paper);
    }

    public QuestionPaper getPaperById(int id) {
        return paperRepository.findById(id).orElse(null);
    }
}