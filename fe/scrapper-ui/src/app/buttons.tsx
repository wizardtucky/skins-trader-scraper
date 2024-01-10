// components/ButtonList.tsx
import React from 'react';

interface ButtonListProps {
    buttons: string[];
    onClick: (buttonText: string) => void;
    isLoading: boolean;
}

const ButtonList: React.FC<ButtonListProps> = ({ buttons, onClick, isLoading }) => {
    return (
        <div style={buttonListStyles}>
            {buttons.map((buttonText) => (
                <button
                    key={buttonText}
                    onClick={() => onClick(buttonText)}
                    style={{
                        ...buttonStyles,
                        backgroundColor: isLoading ? '#adadad' : '#f0f0f0',
                        color: '#000',
                    }}
                    disabled={isLoading}

                >
                    {buttonText}
                </button>
            ))}
        </div>
    );
};

const buttonListStyles = {
    display: 'flex',
    justifyContent: 'center',
    marginBottom: '20px',
};

const buttonStyles = {
    padding: '8px 16px',
    margin: '0 5px',
    border: 'none',
    borderRadius: '5px',
    cursor: 'pointer',
};
export default ButtonList;
