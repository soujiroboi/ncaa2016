import csv
import math
from sets import Set

def newGame(teamOutcome, headerMap, gameDetails):
	game = {}
	game['daynum'] = int(gameDetails[headerMap['Daynum']])
	game['numot'] = int(gameDetails[headerMap['Numot']])

	game['score'] = int(gameDetails[headerMap[teamOutcome + 'score']])
	game['fgm'] = int(gameDetails[headerMap[teamOutcome + 'fgm']])
	game['fga'] = int(gameDetails[headerMap[teamOutcome + 'fga']])
	game['fgp'] = float(game['fgm'] + 1.0) / float(game['fga'] + 1.0)
	game['fgm3'] = int(gameDetails[headerMap[teamOutcome + 'fgm3']])
	game['fga3'] = int(gameDetails[headerMap[teamOutcome + 'fga3']])
	game['fgp3'] = float(game['fgm3'] + 1.0) / float(game['fga3'] + 1.0)
	game['ftm'] = int(gameDetails[headerMap[teamOutcome + 'ftm']])
	game['fta'] = int(gameDetails[headerMap[teamOutcome + 'fta']])
	game['ftp'] = float(game['ftm'] + 1.0) / float(game['fta'] + 1.0)
	game['or'] = int(gameDetails[headerMap[teamOutcome + 'or']])
	game['dr'] = int(gameDetails[headerMap[teamOutcome + 'dr']])
	game['ast'] = int(gameDetails[headerMap[teamOutcome + 'ast']])
	game['to'] = int(gameDetails[headerMap[teamOutcome + 'to']])
	game['stl'] = int(gameDetails[headerMap[teamOutcome + 'stl']])
	game['blk'] = int(gameDetails[headerMap[teamOutcome + 'blk']])
	game['pf'] = int(gameDetails[headerMap[teamOutcome + 'pf']])
	game['win'] = 0

	loc = gameDetails[headerMap['Wloc']]

	if teamOutcome == 'W':
		game['loc'] = loc
		game['win'] = 1
	elif loc == 'H':
		game['loc'] = 'A'
	elif loc == 'A':
		game['loc'] = 'H'
	else:
		game['loc'] = 'N'

	return game


def newTeam():
	newTeam = {}
	newTeam['count'] = 0
	newTeam['games'] = []
	return newTeam


def calculateTotals(games, fields, daynumWeighting=lambda x: 1):

	if len(games) == 0:
		return None

	gameAggregate = {}

	middle = len(games) / 2
	for field in fields:
		games.sort(key=lambda d: d[field])
		gameAggregate[field + 'Median'] = float(games[middle][field])
	
	for field in fields:
		gameAggregate[field + 'Avg'] = 0.0
		gameAggregate[field + 'Std'] = 0.0
		gameAggregate[field + 'Skew'] = 0.0

	total = 0.0

	for game in games:
		total += daynumWeighting(game)
		for field in fields:
			gameAggregate[field + 'Avg'] += daynumWeighting(game) * game[field]

	gameAggregate['fgTotalPercentage'] = 0.0
	if gameAggregate['fgaAvg'] > 0:
		gameAggregate['fgTotalPercentage'] = gameAggregate['fgmAvg'] / gameAggregate['fgaAvg']

	gameAggregate['fg3TotalPercentage'] = 0.0
	if gameAggregate['fga3Avg'] > 0:
		gameAggregate['fg3TotalPercentage'] = gameAggregate['fgm3Avg'] / gameAggregate['fga3Avg']

	gameAggregate['ftTotalPercentage'] = 0.0
	if gameAggregate['ftaAvg'] > 0:
		gameAggregate['ftTotalPercentage'] = gameAggregate['ftmAvg'] / gameAggregate['ftaAvg']

	scorea = (2 * gameAggregate['fgaAvg'] + 3 * gameAggregate['fga3Avg'] + gameAggregate['ftaAvg'])
	gameAggregate['scoreRateTotal'] = 0.0
	if scorea > 0:
		gameAggregate['scoreRateTotal'] = gameAggregate['scoreAvg'] / (2 * gameAggregate['fgaAvg'] + 3 * gameAggregate['fga3Avg'] + gameAggregate['ftaAvg'])

	for field in fields:
		gameAggregate[field + 'Avg'] /= total

	# Calculate Standard Deviation
	for game in games:
		for field in fields:
			gameAggregate[field + 'Std'] += (daynumWeighting(game) * game[field] - gameAggregate[field + 'Avg']) ** 2

	for field in fields:
		gameAggregate[field + 'Std'] = math.sqrt(gameAggregate[field + 'Std'] / total)

	# Calculate Skewness
	for game in games:
		for field in fields:
			gameAggregate[field + 'Skew'] += (daynumWeighting(game) * game[field] - gameAggregate[field + 'Avg']) ** 3

	avg = 1.0 / len(games)
	
	for field in fields:
		if gameAggregate[field + 'Std'] != 0:
			gameAggregate[field + 'Skew'] /= gameAggregate[field + 'Std'] ** 3
			gameAggregate[field + 'Skew'] *= avg

	return gameAggregate


def calculateDifferences(team1, team2, fields):

	diff = {'daynum': team1['daynum']}
	for field in fields:
		diff[field] = team1[field] - team2[field]

	return diff

def parseTeamAvg(filePath, ignoreSet=None, includeSet=None):

	# key is id 
	# value is a dictionary of values
	teams = {}

	regularSeasonWins = Set()

	with open(filePath, 'rb') as csvfile:
		reader = csv.reader(csvfile, delimiter=',')

		headerMap = {}
		headers = reader.next()
		index = 0
		for header in headers:
			headerMap[header] = index
			index += 1

		noFilter = (ignoreSet == None and includeSet == None)
		checkIgnore = ignoreSet != None
		checkInclude = includeSet != None

		for row in reader:
			if(noFilter or (checkIgnore and row[headerMap['Season']] not in ignoreSet) or (checkInclude and row[headerMap['Season']] in includeSet)):

				if row[headerMap['Season']] + '_' + row[headerMap['Wteam']] not in teams:
					teams[row[headerMap['Season']] + '_' + row[headerMap['Wteam']]] = newTeam()

				if row[headerMap['Season']] + '_' + row[headerMap['Lteam']] not in teams:
					teams[row[headerMap['Season']] + '_' + row[headerMap['Lteam']]] = newTeam()
				

				winningGame = newGame('W', headerMap, row)
				losingGame = newGame('L', headerMap, row)

				winningTeam = teams[row[headerMap['Season']] + '_' + row[headerMap['Wteam']]]
				winningTeam['games'].append((winningGame, losingGame))
				

				losingTeam = teams[row[headerMap['Season']] + '_' + row[headerMap['Lteam']]]
				losingTeam['games'].append((losingGame, winningGame))

				regularSeasonWins.add(row[headerMap['Season']] + '_' + row[headerMap['Wteam']] + '_' + row[headerMap['Lteam']])
			
		teamAvg = {}

		fields = ['win', 'score', 'fgm', 'fga', 'fgp', 'fgm3', 'fga3', 'fgp3', 'ftm', 'fta', 'ftp', 'or', 'dr', 'ast', 'to', 'stl', 'blk', 'pf']

		for key in teams:
			allGames = []
			homeGames = []
			awayGames = []
			neutralGames = []
			notHomeGames = []

			allGamesDiff = []
			homeGamesDiff = []
			awayGamesDiff = []
			neutralGamesDiff = []
			notHomeGamesDiff = []

			for gamePair in teams[key]['games']:
				game = gamePair[0]
				gameDiff = calculateDifferences(gamePair[0], gamePair[1], fields)
				allGames.append(game)
				allGamesDiff.append(gameDiff)

				if game['loc'] == 'H':
					homeGames.append(game)
					homeGamesDiff.append(gameDiff)

				elif game['loc'] == 'A':
					awayGames.append(game)
					awayGamesDiff.append(gameDiff)
					notHomeGames.append(game)
					notHomeGamesDiff.append(gameDiff)
				else:
					neutralGames.append(game)
					neutralGames.append(gameDiff)
					notHomeGames.append(game)
					notHomeGamesDiff.append(gameDiff)
			
			weighting = lambda game: 1.0 if game['daynum'] >= 100 else 0.5
			weighting = lambda game: 1.0

			allGameAggregate = calculateTotals(allGames, fields, daynumWeighting=weighting)
			homeGameAggregate = calculateTotals(homeGames, fields, daynumWeighting=weighting)
			awayGameAggregate = calculateTotals(awayGames, fields, daynumWeighting=weighting)
			neutralGameAggregate = calculateTotals(neutralGames, fields, daynumWeighting=weighting)
			notHomeGameAggregate = calculateTotals(notHomeGames, fields, daynumWeighting=weighting)

			allGameDiffAggregate = calculateTotals(allGamesDiff, fields, daynumWeighting=weighting)
			homeGameDiffAggregate = calculateTotals(homeGamesDiff, fields, daynumWeighting=weighting)
			awayGameDiffAggregate = calculateTotals(awayGamesDiff, fields, daynumWeighting=weighting)
			neutralGameDiffAggregate = calculateTotals(neutralGamesDiff, fields, daynumWeighting=weighting)
			notHomeGameDiffAggregate = calculateTotals(notHomeGamesDiff, fields, daynumWeighting=weighting)
			
			teamAvg[key] = {
				'all': allGameAggregate, 'allDiff': allGameDiffAggregate, 
				'home': homeGameAggregate, 'homeDiff': homeGameDiffAggregate, 
				'away': awayGameAggregate, 'awayDiff': awayGameDiffAggregate, 
				'neutral': neutralGameAggregate, 'neutralDiff': neutralGameDiffAggregate, 
				'notHome': notHomeGameAggregate, 'notHomeDiff': notHomeGameDiffAggregate}

		return (teamAvg, regularSeasonWins)


def parseTornament(filePath, regularGamePath, ignoreSet=None, includeSet=None):
	games = []

	with open(filePath, 'rb') as csvfile:
		reader = csv.reader(csvfile, delimiter=',')

		headerMap = {}
		headers = reader.next()
		index = 0
		for header in headers:
			headerMap[header] = index
			index += 1

		noFilter = (ignoreSet == None and includeSet == None)
		checkIgnore = ignoreSet != None
		checkInclude = includeSet != None

		for row in reader:
			if(noFilter or (checkIgnore and row[headerMap['Season']] not in ignoreSet) or (checkInclude and row[headerMap['Season']] in includeSet)):
				games.append((row[headerMap['Season']] + '_' + row[headerMap['Wteam']], row[headerMap['Season']] + '_' + row[headerMap['Lteam']]))
	
	with open(regularGamePath, 'rb') as csvfile:
		reader = csv.reader(csvfile, delimiter=',')

		headerMap = {}
		headers = reader.next()
		index = 0
		for header in headers:
			headerMap[header] = index
			index += 1

		noFilter = (ignoreSet == None and includeSet == None)
		checkIgnore = ignoreSet != None
		checkInclude = includeSet != None

		for row in reader:
			if(noFilter or (checkIgnore and row[headerMap['Season']] not in ignoreSet) or (checkInclude and row[headerMap['Season']] in includeSet)):
				if int(row[headerMap['Daynum']]) > 100 and row[headerMap['Wloc']] == 'N':
					games.append((row[headerMap['Season']] + '_' + row[headerMap['Wteam']], row[headerMap['Season']] + '_' + row[headerMap['Lteam']]))

	return games


def buildFeatures(team1Avg, team2Avg):

	features = []
	fields = []

	baseFields = ['score', 'fgp', 'fgp3', 'ftp', 'or', 'dr', 'ast', 'to', 'stl', 'blk', 'pf']
	stats = ['Avg']
	for baseField in baseFields:
		for stat in stats:
			fields.append(baseField + stat)


	baseFields = ['score', 'fgp', 'fgp3', 'ftp']
	stats = ['Median', 'Std']
	for baseField in baseFields:
		for stat in stats:
			fields.append(baseField + stat)

	fields.append('fgTotalPercentage')
	fields.append('fg3TotalPercentage')
	fields.append('ftTotalPercentage')
	
	locations = ['homeDiff', 'notHomeDiff', 'allDiff']
	for location in locations:
		for field in fields:
			features.append(team1Avg[location][field])
			features.append(team2Avg[location][field])

	fields.append('winAvg')
	locations = (('home', 'home'), ('notHome', 'notHome'), ('all', 'all'), ('home', 'notHome'), ('notHome', 'home'))
	for location in locations:
		for field in fields:
			features.append((team1Avg[location[0]][field] + 1) / (team2Avg[location[1]][field] + 1))
			features.append((team1Avg[location[0]][field]) / (team1Avg[location[0]][field] + team2Avg[location[1]][field]))

	return features


def buildInputAndLabels(tornemementGames, teamAvg, regularSeasonWins):

	inputs = []
	labels = []

	for game in tornemementGames:

		team1Avg = teamAvg[game[0]]
		team2Avg = teamAvg[game[1]]

		inputs.append(buildFeatures(team1Avg, team2Avg))
		labels.append([0])
		inputs.append(buildFeatures(team2Avg, team1Avg))
		labels.append([1])

	return (inputs, labels)


teamAvg = None
regularSeasonWins = None

def getInputsAndLabels(regularSeasonFile, tornamentFile, ignoreSet=None, includeSet=None):
	# Hack to only calculate average once
	global teamAvg
	global regularSeasonWins
	if teamAvg == None:
		(teamAvg, regularSeasonWins) = parseTeamAvg(regularSeasonFile)

	tornamementGames = parseTornament(tornamentFile, regularSeasonFile, ignoreSet=ignoreSet, includeSet=includeSet)
	return buildInputAndLabels(tornamementGames, teamAvg, regularSeasonWins)


def parse_submission_file(file_name):

	game_ids = []

	with open(file_name, 'rb') as csvfile:
		reader = csv.reader(csvfile, delimiter=',')

		headerMap = {}
		headers = reader.next()
		index = 0
		for header in headers:
			headerMap[header] = index
			index += 1

		for row in reader:
			game_str = row[headerMap['Id']]
			game_ids.append(game_str)

	return game_ids


if __name__ == "__main__":

	(inputs, labels) = getInputsAndLabels('project/datav2/RegularSeasonDetailedResults.csv', 'project/datav2/TourneyDetailedResults.csv', ignoreSet='2015')
	print len(inputs[0])

	'''
	(teamAvg, regularSeasonWins) = parseTeamAvg('project/data/RegularSeasonDetailedResults.csv')
	tornamementGames = parseTornament('project/data/TourneyDetailedResults.csv')


	wins = 0
	loses = 0
	count = 0
	for game in tornamementGames:
		count += 1

		y = game[0][0:4]
		t1 = game[0][5:9]
		t2 = game[1][5:9]


		s = y + '_' + t1 + '_' + t2
		if s in regularSeasonWins:
			wins += 1

		s = y + '_' + t2 + '_' + t1
		if s in regularSeasonWins:
			loses += 1

	print wins
	print loses
	print count


	print teamAvg['2003_1421']['home']['scoreAvg']
	print teamAvg['2003_1411']['home']['scoreAvg']

	
	print '2003_1104_1328' in regularSeasonWins
	print '2003_1328_1104' in regularSeasonWins
	'''
