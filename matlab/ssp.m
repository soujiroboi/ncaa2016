%% Calculate steady state probability

%

%% Initialization
clear ; close all; clc

%% ==================== Part 1: Basic Function ====================


%% ======================= Part 2: Plotting =======================
fprintf('Plotting Data ...\n')
data = load('T.txt');
T = data;

[V,D] = eig(T');
[~,ix] = min(abs(diag(D)-1));
v = V(:,ix)';
v = v/sum(v);
v
fid=fopen('Pi.txt','w');
fprintf(fid, '%f \n', v');
fclose(fid);

