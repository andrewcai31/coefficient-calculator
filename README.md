# coefficient-calculator
Naruse-Newton Coefficient Calculator

Program by Andrew Cai
1/2021

Input the non-empty set I, output Naruse-Newton coefficients corresponding to I
Can check unimodality
Can print if each coefficient is greater, less, or equal than the next
Can print rounded ratios between i and (i+1)th Naruse-Newton coefficients

Can also compute Naruse-Newton coefficients of all I such that |I|<=7

Bypasses int/long limit
Time complexity is O(prod(i : I)^2/max(I)^2) -- square of the product of all elements of I except the largest
