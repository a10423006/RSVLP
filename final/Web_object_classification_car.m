clear;
load('/Users/anan/Documents/MATLAB/final/final_Net.mat');
%%
while(true)
    if exist('/Library/WebServer/Documents/car/uploads/car.jpg', 'file') == 2
        car = imread('/Library/WebServer/Documents/car/uploads/car.jpg');

        % Image binary / �Ϲ������G�Ȥ�
        pic = double(imresize(car, [340 594]));

        gray_pic = zeros(size(pic,1),size(pic,2),size(pic,3));
        for j=1:size(pic,1)
            for i= 1:size(pic,2)
                gray_pic(j,i) = round(mean(pic(j,i,:)));
            end
        end

        gray_pic(gray_pic>100) = 255 ;
        gray_pic(gray_pic<=100) = 0 ; % Bindary thresholds = 100
        %imshow(gray_pic)
    end
    %% ����
    number = '';
    y = 2;
    right = 2;
    left = 2;
    while(y <= i - 1) %�e
        for x = 80:290 %��
            if gray_pic(x, y, 1) == 0 %0 �¦� %�q�W��U�Ҭ��զ�h�����
                if all(gray_pic(80:290, y-1, 1) == 255) %�����
                    left = y;
                    break
                end
                if all(gray_pic(80:290, y+1, 1) == 255) %�k���
                    right = y;
                    if right ~= 2 && right > left && left ~= 2 && right - left > 2 %����
                        word_pic = imresize(gray_pic(80:290, left-2:right+2, :),[227 227]);
                        label = classify(final_Net, word_pic);
                        number = strcat(number, char(label(1)));
                    end
                    break
                end
            end
        end
        y = y + 1;
    end
    %%
    fid = fopen('/Library/WebServer/Documents/car/number.txt', 'w'); 
    fprintf(fid, '%c', number);
    fclose(fid);
end