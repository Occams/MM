#!/bin/ruby
require 'matrix'

def onematrix(n)

    v = []
    n.times do |i|
        a = []
        n.times do |i|
            a << 1
        end
        v << a
    end
    
    return Matrix.rows(v)
end

#The first value is later on not scaled by 2.5, but only by 2.. So the value at
# 0,0 is in this matrix (originaly 16) divided by 2.5 and that multiplied by 2
jpegmatrix = [[12.8,11,10,16,24,40,51,61],
[12,12,14,19,26,58,60,55],
[14,13,16,24,40,57,69,56],
[14,17,22,29,51,87,80,62],
[18,22,37,56,68,109,103,77],
[24,35,55,64,81,104,113,92],
[49,64,78,87,103,121,120,101],
[72,92,95,98,112,100,103,99]]

#jpeg chrominanz matrix.
jpegmatrixchrom = [[ 3.2, 4, 6, 11, 24, 24, 24, 24 ],
		    [ 4, 5, 6, 16, 24, 24, 24, 24 ],
		    [ 6, 6, 14, 24, 24, 24, 24, 24 ],
		    [ 11, 16, 24, 24, 24, 24, 24, 24 ],
		    [ 24, 24, 24, 24, 24, 24, 24, 24 ],
		    [ 24, 24, 24, 24, 24, 24, 24, 24 ],
		    [ 24, 24, 24, 24, 24, 24, 24, 24 ],
		    [ 24, 24, 24, 24, 24, 24, 24, 24 ]]


#Calculate the 16x16 quant matrix q
q = []


jpm = Matrix.rows(jpegmatrix)
right_top = jpm[0,7]*onematrix(8)
left_bottom = jpm[7,0]*onematrix(8)
right_bottom = jpm[7,7]*onematrix(8)

#merge
(0..7).each do |i|
    q << (jpm.row(i).to_a+right_top.row(i).to_a)
end

(0..7).each do |i|
    q << (left_bottom.row(i).to_a+right_bottom.row(i).to_a)
end

m = 2.5*Matrix.rows(q)

# Print out m in a java like 2d array
outp = []
(0..15).each do |i|
    outp << m.row(i).to_a.map!{|x| x.to_s + "f"}.join(",")
end

print outp.join(",")
