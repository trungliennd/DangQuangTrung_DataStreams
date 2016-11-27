A = load('Errors.txt');
B = load('exact_Errors.txt');
plot(B,A,'b.');
title('Graph Error and Frequency of word');
xlabel('Exact word frequency F[i]/t (log10)');
ylabel('Erros E[i] = (F_error[i] - F[i])/F[i] (log10)');