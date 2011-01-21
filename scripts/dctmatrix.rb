#!/bin/ruby
# This script computes the coefficient matrix of a dct transform. The size
# of the matrix can be set by N.
require 'matrix'
N = 8

def alpha(i, n)
    if i == 0
        return Math.sqrt(1.0/n)
    else
        return Math.sqrt(2.0/n)
    end
end


arr = []
(0..(N-1)).each do |i|
    arr[i] = []
    (0..(N-1)).each do |j|
        sum = 0.0
        (0..(N-1)).each do |c|
            sum += Math.cos((Math::PI*(2*c+1)*i)/(2*N))
        end
        sum *= alpha(j,N)
        arr[i] << sum
    end
end


M = Matrix.rows(arr)
print M

# Print out m in a java like 2d array
outp = []
(0..(N-1)).each do |i|
    outp << M.row(i).to_a.map!{|x| x.to_s + "f"}.join(",")
end

#print outp.join(",")
