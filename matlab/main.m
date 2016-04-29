%% Linear Regression

% linear regression to get theta

%% Initialization
clear ; close all; clc

%% ==================== Part 1: Basic Function ====================


%% ======================= Part 2: Plotting =======================
fprintf('Plotting Data ...\n')
data = load('P2012-2015.txt');
X = data(:, 1); y = data(:, 2);
m = length(y); % number of training examples
plotData(X, y);

y=y./(1-y);
%plotData(X, y);

y=log(y);

% Plot Data
% Note: You have to complete the code in plotData.m
%plotData(X, y);

fprintf('Program paused. Press enter to continue.\n');
pause;

%% =================== Part 3: Gradient descent ===================
fprintf('Running Gradient Descent ...\n')

X = [ones(m, 1), data(:,1)]; % Add a column of ones to x
theta = zeros(2, 1); % initialize fitting parameters

theta = X\y;

yCalc1 = X*theta;
X=X(:,2);
%scatter(X,y)
%hold on
%plot(X,yCalc1)

%hold off % don't overlay any more plots on this figure

fprintf('Program paused. Press enter to continue.\n');


A = data(:, 1); B = data(:, 2);
scatter(A,B)
hold on
C = 1./(1+1./exp(yCalc1));
plot(A,C)

hold off