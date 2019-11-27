fileFolder = fullfile('/Users', 'anan', 'Documents', 'MATLAB', 'img','0');
dirOutput = dir(fullfile(fileFolder, '*.jpg'));

fileNames = {dirOutput.name};
mkdir ../MATLAB/img/0

for allimage = 1:length(fileNames)
    img_orig_filename = fileNames{allimage};
    img_orig = imread(img_orig_filename);
    img_resize = imresize(img_orig, [227 227]);
    folder = '/Users/anan/Documents/MATLAB/img/0/';
    newimagename = [folder img_orig_filename];
    imwrite(img_resize, newimagename);
end