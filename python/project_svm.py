from sklearn import svm
import numpy as np
import parse_data

if __name__ == "__main__":

    (teamAvg, regularSeasonWins) = parse_data.parseTeamAvg('../datav2/RegularSeasonDetailedResults.csv')
    #games = parse_data.parse_submission_file('project/datav2/SampleSubmission.csv')


    testSet = None
    (trainData, trainLabels) = parse_data.getInputsAndLabels('../datav2/RegularSeasonDetailedResults.csv', '../datav2/TourneyDetailedResults.csv', ignoreSet=testSet)
    (testData, testLabels) = parse_data.getInputsAndLabels('../datav2/RegularSeasonDetailedResults.csv', '../datav2/TourneyDetailedResults.csv', includeSet=testSet)

    X = trainData
    y = np.array(trainLabels).reshape(len(trainLabels))
    print len(trainLabels)

    clf = svm.SVC(C=1.0, cache_size=200, class_weight=None, coef0=0.0,
        degree=3, gamma='auto', kernel='rbf',
        max_iter=-1, probability=True, random_state=None, shrinking=True,
        tol=0.001, verbose=False)
    clf.fit(X, y)  

    #print clf.predict([testData[0]])
    #print testLabels[0]

    correct = 0
    count = 0
    for i in range(0, len(testLabels)):
        count += 1

        pred = 1
        prob = clf.predict_proba([testData[i]])[0][0];
        if prob > 0.5:
            pred = 0


        if (pred == testLabels[i][0]):
            correct += 1

        #print "{} {}".format(clf.predict_proba([testData[i]])[0][0], testLabels[i][0])

    print float(correct) / count
    print count
   
    games = parse_data.parse_submission_file('../datav2/SampleSubmission.csv')
    inputs = []

    for game_str in games:
        team1 = game_str[0:9]
        team2 = game_str[0:5] + game_str[10:16]

        team1Avg = teamAvg[team1]
        team2Avg = teamAvg[team2]

        inputs.append(parse_data.buildFeatures(team1Avg, team2Avg))

    f = open('submission.csv', 'w')
    f.write("Id,Pred\n")
    for i in range(0, len(games)):
        prediction = clf.predict_proba([inputs[i]])[0][0]
        #print "{}, {}".format(games[i], prediction)
        f.write("{}, {}\n".format(games[i], prediction))
    
