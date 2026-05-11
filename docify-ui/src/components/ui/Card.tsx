import React, { PropsWithChildren } from 'react';
import { cn } from '../../lib/utils';

interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  hoverEffect?: boolean;
}

export const Card = ({ className, children, hoverEffect = false, ...props }: PropsWithChildren<CardProps>) => {
  return (
    <div
      className={cn(
        'bg-white border border-border rounded-2xl p-6 transition-all duration-300 card-shadow',
        hoverEffect && 'hover:shadow-md hover:border-primary/20',
        className
      )}
      {...props}
    >
      {children}
    </div>
  );
};
