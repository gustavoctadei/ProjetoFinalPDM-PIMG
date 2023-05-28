import cv2
import numpy

def filtro_bilateral_opencv(img):
    img_out = cv2.bilateralFilter(img, 3, 86.585, 28.7564)
    
    return img_out

def funcao_gaussiana(x,sigma):
    return (1.0/(2*numpy.pi*(sigma**2)))*numpy.exp(-(x**2)/(2*(sigma**2)))

def espaco(x1,y1,x2,y2):
    return numpy.sqrt(numpy.abs((x1-x2)**2-(y1-y2)**2))

def filtro_bilateral(img):
    dimensao_janela = 3
    
    #Parametros utilizados pelo artigo
    sigma_i = 86.585
    sigma_s = 28.7564
    
    img_out = numpy.zeros(img.shape)

    for lin in range(len(img)):
        for col in range(len(img[0])):
            wp_total = 0
            imagem_filtrada = 0
            
            for k in range(dimensao_janela):
                for l in range(dimensao_janela):
                    n_x =lin - (dimensao_janela/2 - k)
                    n_y =col - (dimensao_janela/2 - l)
                    
                    if n_x >= len(img):
                        n_x -= len(img)
                    
                    if n_y >= len(img[0]):
                        n_y -= len(img[0])
                    
                    gi = funcao_gaussiana(img[int(n_x)][int(n_y)] - img[lin][col], sigma_i)
                    gs = funcao_gaussiana(espaco(n_x, n_y, lin, col), sigma_s)
                    wp = gi * gs
                    imagem_filtrada = (imagem_filtrada) + (img[int(n_x)][int(n_y)] * wp)
                    wp_total = wp_total + wp
            
            imagem_filtrada = imagem_filtrada // wp_total
            imagem_filtrada = imagem_filtrada.astype(int)
            img_out[lin][col] = imagem_filtrada
    return img_out